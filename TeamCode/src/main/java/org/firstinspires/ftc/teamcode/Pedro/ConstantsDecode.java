package org.firstinspires.ftc.teamcode.Pedro;

import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ConstantsDecode {

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.5584405897733354);
                Controller secondaryTranslationalForward = Controller.proportional(0.20632887611386982);
                Controller primaryTranslationalLateral = Controller.proportional(1.126224128077316);
                Controller secondaryTranslationalLateral = Controller.proportional(0.41610972206163055);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.013098337555642468));
                c.brake.set(Controller.proportionalFeedforward(0.011133586922296098));

                c.headingFeedback.set(Controller.proportional(8.671465667145917));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.07339220630227516, 0.006859957538411244));

                c.linearBrakeCoefficients.set(Matrix.diag(0.15899263914570266, 0.048539631719911935));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0013378711158710745, 0.0025696484933031205));

                c.maxAchievableForwardVelocity.set(39.36239120567458);
                c.maxAchievableStrafeVelocity.set(32.235486134630044);
                c.naturalForwardDeceleration.set(32.68057926229764);
                c.naturalStrafeDeceleration.set(63.332597032903756);
            }
    );

    // Same as foresightConfig, except old pathConstraintsFarAuto used brakingStrength 1.5 instead of 1.6 —
    // since that field's 3.0 mapping is itself unconfirmed above, this is currently identical to foresightConfig.
    public static ForesightConfig foresightConfigFarAuto = foresightConfig;

    public static String frontRightName         = "frontright";
    public static String frontLeftName          = "frontleft";
    public static String backRightName          = "backright";
    public static String backLeftName           = "backleft";
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set(frontLeftName);
        c.frontRightName.set(frontRightName);
        c.backLeftName.set(backLeftName);
        c.backRightName.set(backRightName);

        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        // useVoltageCompensation/nominalVoltage — no equivalent field in 3.0's MecanumConfig at all.
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
        c.xPodOffset.set(-4.4055);   // was forwardPodY, -0.1119 m converted to inches
        c.yPodOffset.set(-7.2696);   // was strafePodX, -0.18467 m converted to inches
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);  // was forwardEncoderDirection
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD); // was strafeEncoderDirection
        // yawScalar(1.000977114) — no PinpointConfig field exists; set directly on the raw driver below.
    });

    private static void applyYawScalar(HardwareMap hardwareMap) {
        GoBildaPinpointDriver pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setYawScalar(1.000977114);
    }

    public static Follower createFollowerDecode(HardwareMap hardwareMap) {
        applyYawScalar(hardwareMap);
        Mecanum drivetrain = new Mecanum(hardwareMap, drivetrainConfig);
        PinpointLocalizer localizer = new PinpointLocalizer(hardwareMap, localizerConfig);
        Foresight algorithm = new Foresight(foresightConfig);
        return new Follower(localizer, drivetrain, algorithm);
    }

    public static Follower createFollowerDecodeFarAuto(HardwareMap hardwareMap) {
        applyYawScalar(hardwareMap);
        Mecanum drivetrain = new Mecanum(hardwareMap, drivetrainConfig);
        PinpointLocalizer localizer = new PinpointLocalizer(hardwareMap, localizerConfig);
        Foresight algorithm = new Foresight(foresightConfigFarAuto);
        return new Follower(localizer, drivetrain, algorithm);
    }
}