package ro.sparktech24345.logicore.hardware;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import ro.sparktech24345.logicore.config.Keys;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.utils.CachingTracker;
import ro.sparktech24345.logicore.utils.MathUtils;

/**
 * DcMotorEx wrapper that only sends a power to the hub when it changed by more than
 * {@code threshold} (see {@link CachingTracker}), and only sends zero power behavior when it changed.
 * Powers go through {@link CoreOpMode#setMotorPower} when an OpMode is running, so Blaze bulk
 * writes keep working. Everything else is passed straight to the wrapped motor.
 * <p>
 * Call {@link #resetCache()} after something else may have written to the same motor
 * (e.g. a DriveMode handover), so the next {@code setPower} is always sent.
 */
public class CachingMotor implements DcMotorEx {
    public static final double DEFAULT_THRESHOLD = 0.02;

    private final DcMotorEx motor;
    private final int key;
    private double threshold;
    private CachingTracker tracker;

    public CachingMotor(DcMotorEx motor) {
        this(motor, DEFAULT_THRESHOLD);
    }

    public CachingMotor(DcMotorEx motor, double threshold) {
        this.motor = motor;
        this.threshold = threshold;
        this.key = Keys.key(motor.getController().getConnectionInfo(), motor.getPortNumber());
        resetCache();
    }

    /** The wrapped SDK motor, for anything that must bypass the cache. */
    public DcMotorEx motor() {
        return motor;
    }

    /** Hub/port key used by LogiCore's MotorMap. */
    public int key() {
        return key;
    }

    public double threshold() {
        return threshold;
    }

    public void threshold(double threshold) {
        this.threshold = threshold;
        tracker.threshold(threshold);
    }

    public CachingTracker tracker() {
        return tracker;
    }

    /** Forget the cached power and zero power behavior; the next writes are always sent. */
    public void resetCache() {
        tracker = new CachingTracker(threshold, Double.NaN);
    }

    // ===================== cached writes =====================

    @Override
    public void setPower(double power) {
        power = MathUtils.clip(power, -1, 1);
        if (!tracker.shouldUpdate(power)) return;
        CoreOpMode op = CoreOpMode.instance();
        if (op != null) op.setMotorPower(key, motor, power);
        else motor.setPower(power);
    }

    /** Last power that was actually sent (in Blaze mode the SDK's own getPower() isn't updated). */
    @Override
    public double getPower() {
        return tracker.lastValue();
    }

    @Override
    public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {
        motor.setZeroPowerBehavior(zeroPowerBehavior);
    }

    // writes that change what a given power means: send them and invalidate the power cache

    @Override
    public void setDirection(Direction direction) {
        if (direction != motor.getDirection()) resetPowerCache();
        motor.setDirection(direction);
    }

    @Override
    public void setMode(RunMode mode) {
        resetPowerCache();
        motor.setMode(mode);
    }

    @Override
    public void setPowerFloat() {
        resetPowerCache();
        motor.setPowerFloat();
    }

    @Override
    public void setMotorEnable() {
        resetPowerCache();
        motor.setMotorEnable();
    }

    @Override
    public void setMotorDisable() {
        resetPowerCache();
        motor.setMotorDisable();
    }

    private void resetPowerCache() {
        tracker = new CachingTracker(threshold, Double.NaN);
    }

    // ===================== pass-through =====================

    @Override public boolean isMotorEnabled() { return motor.isMotorEnabled(); }
    @Override public void setVelocity(double angularRate) { motor.setVelocity(angularRate); }
    @Override public void setVelocity(double angularRate, AngleUnit unit) { motor.setVelocity(angularRate, unit); }
    @Override public double getVelocity() { return motor.getVelocity(); }
    @Override public double getVelocity(AngleUnit unit) { return motor.getVelocity(unit); }
    @SuppressWarnings("deprecation")
    @Override public void setPIDCoefficients(RunMode mode, PIDCoefficients pidCoefficients) { motor.setPIDCoefficients(mode, pidCoefficients); }
    @Override public void setPIDFCoefficients(RunMode mode, PIDFCoefficients pidfCoefficients) { motor.setPIDFCoefficients(mode, pidfCoefficients); }
    @Override public void setVelocityPIDFCoefficients(double p, double i, double d, double f) { motor.setVelocityPIDFCoefficients(p, i, d, f); }
    @Override public void setPositionPIDFCoefficients(double p) { motor.setPositionPIDFCoefficients(p); }
    @SuppressWarnings("deprecation")
    @Override public PIDCoefficients getPIDCoefficients(RunMode mode) { return motor.getPIDCoefficients(mode); }
    @Override public PIDFCoefficients getPIDFCoefficients(RunMode mode) { return motor.getPIDFCoefficients(mode); }
    @Override public void setTargetPositionTolerance(int tolerance) { motor.setTargetPositionTolerance(tolerance); }
    @Override public int getTargetPositionTolerance() { return motor.getTargetPositionTolerance(); }
    @Override public double getCurrent(CurrentUnit unit) { return motor.getCurrent(unit); }
    @Override public double getCurrentAlert(CurrentUnit unit) { return motor.getCurrentAlert(unit); }
    @Override public void setCurrentAlert(double current, CurrentUnit unit) { motor.setCurrentAlert(current, unit); }
    @Override public boolean isOverCurrent() { return motor.isOverCurrent(); }

    @Override public MotorConfigurationType getMotorType() { return motor.getMotorType(); }
    @Override public void setMotorType(MotorConfigurationType motorType) { motor.setMotorType(motorType); }
    @Override public DcMotorController getController() { return motor.getController(); }
    @Override public int getPortNumber() { return motor.getPortNumber(); }
    @Override public ZeroPowerBehavior getZeroPowerBehavior() { return motor.getZeroPowerBehavior(); }
    @Override public boolean getPowerFloat() { return motor.getPowerFloat(); }
    @Override public void setTargetPosition(int position) { motor.setTargetPosition(position); }
    @Override public int getTargetPosition() { return motor.getTargetPosition(); }
    @Override public boolean isBusy() { return motor.isBusy(); }
    @Override public int getCurrentPosition() { return motor.getCurrentPosition(); }
    @Override public RunMode getMode() { return motor.getMode(); }
    @Override public Direction getDirection() { return motor.getDirection(); }

    @Override public Manufacturer getManufacturer() { return motor.getManufacturer(); }
    @Override public String getDeviceName() { return motor.getDeviceName(); }
    @Override public String getConnectionInfo() { return motor.getConnectionInfo(); }
    @Override public int getVersion() { return motor.getVersion(); }
    @Override public void resetDeviceConfigurationForOpMode() { motor.resetDeviceConfigurationForOpMode(); resetCache(); }
    @Override public void close() { motor.close(); }
}
