package org.firstinspires.ftc.teamcode.Components;

import static com.pedropathing.utils.Utils.lerp;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;

import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.hardware.CoreMotor;
import ro.sparktech24345.logicore.hardware.Controllers.PIDController;

/**
 * Turret rotation: PD position control with a feedforward on the robot's angular velocity,
 * so the turret keeps pointing at the goal while the robot turns.
 * Refactored from the old repo's Experimental TurretComponent (MotorComponent subclass).
 * <p>
 * The turret angle is in "turret degrees" in [MIN_ANGLE, MAX_ANGLE] (encoder ticks / ticksPerDegree).
 * Either call {@link #setTargetAngle(double)} directly, or {@link #aimAt(double, double)} to have the
 * component compute the angle from the follower pose every loop.
 */
@Config
public class DecodeTurretComponent implements CoreModule {
    // Tunables (FTC Dashboard). Defaults are the values from the old robot's ComponentMakerMethods / teleop.
    public static double kP = 0.035, kI = 0, kD = 0.0015;
    /**
     * Power per (deg/s) of robot angular velocity, fights heading lag.
     */
    public static double kV = 0.003;
    public static double kF = 0;
    /**
     * Static friction kick, applied while error is above {@link #staticKickThreshold}.
     */
    public static double kStatic = 0.06;
    public static double staticKickThreshold = 0.25;
    /**
     * Encoder ticks per turret degree (old code: setResolution(5)).
     */
    public static double ticksPerDegree = 5;
    public static double MIN_ANGLE = 0, MAX_ANGLE = 360;
    /**
     * Seconds to extrapolate the robot position by, to compensate for the ball's flight time.
     */
    public static double lookaheadSeconds = 0.45;
    /**
     * Added to the computed aim angle (replaces the old teleop's rotationAdder / farZoneCameraAdder).
     */
    public static double aimOffsetDegrees = 0;
    /**
     * The old teleop subtracted the field-relative angle (flagged with a TODO); flip if the turret aims mirrored.
     */
    public static boolean invertAim = true;
    /**
     * Camera offset from the robot center, forward along the heading (old: x_offset).
     */
    public static double cameraForwardOffset = 8;
    public static double error;

    public final CoreMotor<EmptyStateSet> rotationMotor =
            new CoreMotor<>(GlobalStorage.turretRotationMotorName, EmptyStateSet.ZERO);

    private final PIDController pid = new PIDController(kP, kI, kD, kF);
    private CoreOpMode instance = null;

    private double targetAngle = 0;
    private boolean enabled = true;
    private boolean autoAim = false;
    private double aimX = 130, aimY = 53;

    // Robot motion estimate, derived from the follower pose
    private double lastX, lastY, lastHeadingDeg;
    private long lastNanos = 0;
    private double vx = 0, vy = 0, robotAngularVel = 0;
    private double currentX, currentY, currentHeadingDeg;
    private double D2_rotationAdder = 0;
    private double usedAimX, usedAimY;
    private double targetXCenter = 128; // 130
    private double targetYCenter = 53;

    private double targetXRightPanel = 127;
    private double targetYRightPanel = 48;
    private double targetXLeftPanel = 123.5;
    private double targetYLeftPanel = 53;

    private static double wrapDegrees(double deg) {
        while (deg > 180) deg -= 360;
        while (deg < -180) deg += 360;
        return deg;
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    // ================== Control ==================

    /**
     * Called once during OpMode initialization - set up hardware and initial state
     */
    public void initCore() {
        instance = CoreOpMode.instance();
        instance.install(rotationMotor, 1);
        rotationMotor.encoded(true);
        rotationMotor.zeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // ================== Pose tracking ==================

    /**
     * Called every loop cycle - update module logic
     */
    public void loopCore() {
        updateRobotPose(instance.follower().pose());

        if (!enabled) {
            rotationMotor.motorPower(0);
            return;
        }
        calculateTargetPosition();
        if (autoAim) targetAngle = calculateAimAngle(usedAimX, usedAimY, lookaheadSeconds);
        rotationMotor.motorPower(calculatePower());
    }

    // ================== Aiming ==================

    private void calculateTargetPosition(){
        usedAimX = aimX;
        usedAimY = aimY;

        double robotToGoalAbsoluteAngle = Math.abs(angleFromTargetToRobot(aimX, aimY));
        if (robotToGoalAbsoluteAngle < 30) {
            // alpha is 1.0 at 0 degrees (Full Right) and 0.0 at 30 degrees (Center)
            double alpha = (30.0 - robotToGoalAbsoluteAngle) / 30.0;
            usedAimX = lerp(targetXCenter, targetXRightPanel, alpha);
            usedAimY = lerp(targetYCenter, targetYRightPanel, alpha);

        } else if (robotToGoalAbsoluteAngle > 65) {
            // alpha is 0.0 at 65 degrees (Center) and 1.0 at 90+ degrees (Full Left)
            // clamp alpha between 0 and 1 to prevent the target from sliding off the goal
            double alpha = Math.min(1.0, (robotToGoalAbsoluteAngle - 65.0) / 25.0);
            usedAimX = lerp(targetXCenter, targetXLeftPanel, alpha);
            usedAimY = lerp(targetYCenter, targetYLeftPanel, alpha);

        } else {
            //if between 30 and 65, stay locked on Center
            usedAimX = targetXCenter;
            usedAimY = targetYCenter;
        }
    }

    private double angleFromTargetToRobot(double targetX, double targetY) {
        // from pose to point

        double dx = currentX - aimX;
        double dy = currentY - aimY;

        // atan2(dx, dy) gives an angle in radians in the range [-PI, PI]
        // matching:
        // same X -> 0 or 180
        // same Y -> +90 or -90
        // left negative, right positive
        double angleRadians = Math.atan2(dx, dy);

        return Math.toDegrees(angleRadians) + 90;
    }
    private double calculatePower() {
        pid.setPIDFConstants(kP, kI, kD, kF);

        double target = clamp(targetAngle, MIN_ANGLE, MAX_ANGLE);
        double current = getAngle();
        error = target - current;

        double output = pid.calculate(target, current);
//        if (Math.abs(error) > staticKickThreshold) output += Math.signum(output) * kStatic;
        return clamp(output, -1, 1);
    }

    /**
     * Estimates robot velocity and angular velocity from consecutive poses.
     */
    public void updateRobotPose(Pose robotPose) {
        double x = robotPose.x();
        double y = robotPose.y();
        double headingDeg = Math.toDegrees(robotPose.heading());

        currentX = x;
        currentY = y;
        currentHeadingDeg = headingDeg;

        long now = System.nanoTime();
        double dt = (now - lastNanos) / 1e9;
        if (lastNanos != 0 && dt > 0) {
            robotAngularVel = wrapDegrees(headingDeg - lastHeadingDeg) / dt;
            vx = (x - lastX) / dt;
            vy = (y - lastY) / dt;
        }
        lastX = x;
        lastY = y;
        lastHeadingDeg = headingDeg;
        lastNanos = now;
    }

    /**
     * Turret angle needed to face (targetX, targetY), extrapolating the robot position by
     * lookaheadSeconds using its current velocity. Result is in [0, 360).
     */
    public double calculateAimAngle(double targetX, double targetY, double lookaheadSeconds) {
        double predX = currentX + vx * lookaheadSeconds;
        double predY = currentY + vy * lookaheadSeconds;
        return toTurretAngle(predX, predY, currentHeadingDeg, targetX, targetY);
    }

    // ================== API ==================

    /**
     * Same as {@link #calculateAimAngle} but from the camera's pose (e.g. a Limelight/odometry fused pose)
     * instead of the follower pose. The camera sits {@link #cameraForwardOffset} ahead of the robot center.
     * Fixes the old calculateTurretCamera, which mixed degrees and radians for the heading.
     */
    public double calculateAimAngleFromCamera(Pose cameraPose, double targetX, double targetY) {
        double headingRad = cameraPose.heading();
        double camX = cameraPose.x() + cameraForwardOffset * Math.cos(headingRad);
        double camY = cameraPose.y() + cameraForwardOffset * Math.sin(headingRad);
        return toTurretAngle(camX, camY, Math.toDegrees(headingRad), targetX, targetY);
    }

    private double toTurretAngle(double fromX, double fromY, double headingDeg, double targetX, double targetY) {
        double worldAngle = Math.toDegrees(Math.atan2(targetY - fromY, targetX - fromX));
        double relative = wrapDegrees(worldAngle - headingDeg);
        relative += D2_rotationAdder;
        double angle = (invertAim ? -relative : relative) + aimOffsetDegrees;
        return ((angle % 360) + 360) % 360;
    }

//    public static double distanceToVelocityFunction(double distance) {
//        if (distance <= 1.0) return closeVelo;
//        if (distance > 2.9)  return grade1farVelo * distance + grade0farVelo + farVeloOffset;
//
//        return grade1VeloClose * distance + grade0VeloClose;
//    }

    // ================== API ==================

    /** Manual aim: stops auto-aiming and holds this turret angle. */

    /** Continuously aim at a field point (recomputed every loop from the follower pose). */
    public void aimAt(double x, double y) {
        autoAim = true;
        aimX = x;
        aimY = y;
    }

    public void startAiming(double x) {
        autoAim = true;
        D2_rotationAdder = x;
    }

    public void decideCoordinates() {

    }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public double getAngle() {
        double ticks = rotationMotor.currentPosition();
        return Double.isNaN(ticks) ? 0 : ticks / ticksPerDegree;
    }

    public double getTargetAngle() {
        return targetAngle;
    }

    /**
     * Manual aim: stops auto-aiming and holds this turret angle.
     */
    public void setTargetAngle(double degrees) {
        autoAim = false;
        targetAngle = degrees;
    }

    public double getError() {
        return targetAngle - getAngle();
    }

    public double getVx() {
        return vx;
    }

    // ================== Helpers ==================

    public double getVy() {
        return vy;
    }

    public double getRobotAngularVelocity() {
        return robotAngularVel;
    }
}
