package ro.sparktech24345.logicore.utils;


/**
 * Throttling mechanism for operations that don't need to run every loop cycle.
 * Useful for sensor updates, telemetry, and other expensive operations.
 */
public class TickInterval {
    public TickInterval(double interval) {
        this(interval, 0);
    }

    public TickInterval(double interval, double delay) {
        this.interval = interval;
        this.delay = delay;
    }

    private final double interval;
    private final double delay;

    public enum IntervalMode {
        TICKS,
        TIME,
    }

    private long ticks = 0;
    private final PreciseTimer timer = new PreciseTimer().start();
    private boolean firstTick = true;

    public void firstTick(boolean value) {
        this.firstTick = value;
    }

    public boolean firstTick() {
        return this.firstTick;
    }

    private IntervalMode mode = IntervalMode.TICKS;

    public void mode(IntervalMode value) {
        this.mode = value;
    }

    public IntervalMode mode() {
        return this.mode;
    }

    private TimeUnit timeUnit = TimeUnit.MILLIS;

    public void timeUnit(TimeUnit value) {
        this.timeUnit = value;
    }

    public TimeUnit timeUnit() {
        return this.timeUnit;
    }

    /**
     * Check if the current tick should trigger an update.
     * Note: This increments the internal counter, so it should be called exactly once per loop.
     *
     * @return true if an update is due
     */
    public boolean shouldTick() {
        switch (mode) {
            case TICKS: {
                ticks %= (long) interval;
                boolean update = ticks == (long) delay;
                ++ticks;
                return update;
            }
            case TIME: {
                double elapsed = timer.time().get(timeUnit);
                if (elapsed >= interval) {
                    timer.start();
                    return true;
                } else return false;
            }
            default:
                return false;
        }
    }
}