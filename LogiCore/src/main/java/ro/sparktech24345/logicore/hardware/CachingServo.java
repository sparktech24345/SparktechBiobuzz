package ro.sparktech24345.logicore.hardware;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import ro.sparktech24345.logicore.utils.CachingTracker;

/**
 * Servo wrapper that only sends a position when it changed by more than {@code threshold}
 * (see {@link CachingTracker}). The default threshold 0 skips only exact repeats.
 * Everything else is passed straight to the wrapped servo.
 * <p>
 * Call {@link #resetCache()} after something else may have moved the same servo.
 */
public class CachingServo implements Servo {
    public static final double DEFAULT_THRESHOLD = 0;

    private final Servo servo;
    private double threshold;
    private CachingTracker tracker;

    public CachingServo(Servo servo) {
        this(servo, DEFAULT_THRESHOLD);
    }

    public CachingServo(Servo servo, double threshold) {
        this.servo = servo;
        this.threshold = threshold;
        resetCache();
    }

    /** The wrapped SDK servo, for anything that must bypass the cache. */
    public Servo servo() {
        return servo;
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

    /** Forget the cached position; the next setPosition is always sent. */
    public void resetCache() {
        tracker = new CachingTracker(threshold, Double.NaN);
    }

    @Override
    public void setPosition(double position) {
        if (tracker.shouldUpdate(position)) servo.setPosition(position);
    }

    // writes that change what a given position means: send them and invalidate the cache

    @Override
    public void setDirection(Direction direction) {
        if (direction != servo.getDirection()) resetCache();
        servo.setDirection(direction);
    }

    @Override
    public void scaleRange(double min, double max) {
        resetCache();
        servo.scaleRange(min, max);
    }

    // ===================== pass-through =====================

    @Override public double getPosition() { return servo.getPosition(); }
    @Override public Direction getDirection() { return servo.getDirection(); }
    @Override public ServoController getController() { return servo.getController(); }
    @Override public int getPortNumber() { return servo.getPortNumber(); }

    @Override public Manufacturer getManufacturer() { return servo.getManufacturer(); }
    @Override public String getDeviceName() { return servo.getDeviceName(); }
    @Override public String getConnectionInfo() { return servo.getConnectionInfo(); }
    @Override public int getVersion() { return servo.getVersion(); }
    @Override public void resetDeviceConfigurationForOpMode() { servo.resetDeviceConfigurationForOpMode(); resetCache(); }
    @Override public void close() { servo.close(); }
}
