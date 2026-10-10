package ro.sparktech24345.logicore.pedro;

import android.annotation.SuppressLint;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.drivetrain.Drivetrain;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.utils.Utils;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.HashMap;
import java.util.Map;

import dev.frozenmilk.dairy.cachinghardware.CachingDcMotorEx;
import ro.sparktech24345.logicore.config.Keys;
import ro.sparktech24345.logicore.core.CoreOpMode;

public class SparkMecanum implements Drivetrain {
    public final MecanumConfig config;
    private final DcMotorEx[] motors;
    public final double[] wheelPowers = new double[4];

    private static final int FL = 0;
    private static final int FR = 1;
    private static final int BL = 2;
    private static final int BR = 3;

    private int[] keys = {0, 0, 0, 0};

    private double powerScale = 1.0;
    private DrivePowers drivePowers = DrivePowers.zero();

    public SparkMecanum(HardwareMap map, MecanumConfig config) {
        this.config = config;

        motors = new DcMotorEx[]{
                map.get(DcMotorEx.class, config.frontLeftName.get()),
                map.get(DcMotorEx.class, config.frontRightName.get()),
                map.get(DcMotorEx.class, config.backLeftName.get()),
                map.get(DcMotorEx.class, config.backRightName.get())
        };

        motors[FL].setDirection(config.frontLeftDirection.get());
        motors[FR].setDirection(config.frontRightDirection.get());
        motors[BL].setDirection(config.backLeftDirection.get());
        motors[BR].setDirection(config.backRightDirection.get());

        keys[FL] = Keys.key(motors[FL].getController().getConnectionInfo(), motors[FL].getPortNumber());
        keys[FR] = Keys.key(motors[FR].getController().getConnectionInfo(), motors[FR].getPortNumber());
        keys[BL] = Keys.key(motors[BL].getController().getConnectionInfo(), motors[BL].getPortNumber());
        keys[BR] = Keys.key(motors[BR].getController().getConnectionInfo(), motors[BR].getPortNumber());

        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @SuppressLint("DefaultLocale")
    public void applyDrive(DrivePowers powers) {
        drivePowers = powers;

        double[] wheelPowers = computeWheelPowersUnnormalized(powers);

        double maxPower = 1.0;
        for (double power : wheelPowers) {
            maxPower = Math.max(maxPower, Math.abs(power));
        }

        powerScale = 1.0 / maxPower;

        for (int i = 0; i < wheelPowers.length; i++) {
            this.wheelPowers[i] = wheelPowers[i] / maxPower;
        }

        if (!CoreOpMode.instance().config().useDriveTrain.get()) {
            CoreOpMode.instance().setMotorPower(keys[FL], motors[FL], this.wheelPowers[FL]);
            CoreOpMode.instance().setMotorPower(keys[FR], motors[FR], this.wheelPowers[FR]);
            CoreOpMode.instance().setMotorPower(keys[BR], motors[BR], this.wheelPowers[BR]);
            CoreOpMode.instance().setMotorPower(keys[BL], motors[BL], this.wheelPowers[BL]);
        }
    }

    public double[] computeWheelPowersUnnormalized(DrivePowers powers) {
        double[] wheelPowers = new double[4];
        double forward = powers.forward();
        double strafe = powers.strafe();
        double turn = powers.turn();

        double fl = forward - strafe - turn;
        double fr = forward + strafe + turn;
        double bl = forward + strafe - turn;
        double br = forward - strafe + turn;

        wheelPowers[FL] = fl;
        wheelPowers[FR] = fr;
        wheelPowers[BL] = bl;
        wheelPowers[BR] = br;

        return wheelPowers;
    }

    @Override
    public double maxScaling(DrivePowers current, DrivePowers delta) {
        double lambda = 1.0;

        double[] currentPowers = computeWheelPowersUnnormalized(current);
        double[] deltaPowers = computeWheelPowersUnnormalized(delta);

        for (int i = 0; i < 4; i++) {
            double a = currentPowers[i];
            double b = deltaPowers[i];

            if (Math.abs(b) < 1e-9) continue;

            double t1 = (1.0 - a) / b;
            double t2 = (-1.0 - a) / b;

            if (t1 >= 0.0 && t1 < lambda) lambda = t1;
            if (t2 >= 0.0 && t2 < lambda) lambda = t2;
        }

        return Utils.clamp(lambda, 0.0, 1.0);
    }

    @Override
    public void drive(DrivePowers powers, boolean manual) {
        if (manual && config.manualBrakeMode.get())
            setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        else
            setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        applyDrive(powers);
    }

    @Override
    public void stop() {
        stop(config.manualBrakeMode.get());
    }

    public void stop(boolean brake) {
        if (brake) {
            setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        } else {
            setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        if (!CoreOpMode.instance().config().useDriveTrain.get()) {
            CoreOpMode.instance().setMotorPower(keys[FL], motors[FL], 0);
            CoreOpMode.instance().setMotorPower(keys[FR], motors[FR], 0);
            CoreOpMode.instance().setMotorPower(keys[BR], motors[BR], 0);
            CoreOpMode.instance().setMotorPower(keys[BL], motors[BL], 0);
        }
    }

    @Override
    public Map<String, Object> debug() {
        Map<String, Object> map = new HashMap<>();

        map.put("forward", drivePowers.forward());
        map.put("strafe", drivePowers.strafe());
        map.put("turn", drivePowers.turn());
        map.put("powerScale", powerScale);
        map.put("leftFrontWheelPower", wheelPowers[FL]);
        map.put("rightFrontWheelPower", wheelPowers[FR]);
        map.put("leftBackWheelPower", wheelPowers[BL]);
        map.put("rightBackWheelPower", wheelPowers[BR]);

        return map;
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        for (DcMotorEx motor : motors) {
            motor.setZeroPowerBehavior(behavior);
        }
    }

    /**
     * Returns the sum of the four motors current in Amps
     * This is not bulk cached by the motors so each motor request is a hardware read
     */
    public double currentAmps() {
        double total = 0;
        for (DcMotorEx motor : motors) {
            total += motor.getCurrent(CurrentUnit.AMPS);
        }
        return total;
    }

    @Override
    public double interpolateVelocity(double xRadius, double yRadius, double theta) {
        return 1.0 / (Math.abs(Math.cos(theta)) / xRadius + Math.abs(Math.sin(theta)) / yRadius);
    }
}
