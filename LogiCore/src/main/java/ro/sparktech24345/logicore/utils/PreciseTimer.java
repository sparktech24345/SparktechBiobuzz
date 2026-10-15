package ro.sparktech24345.logicore.utils;

/**
 * High-precision timer for benchmarking and timing operations.
 * Uses nanosecond precision for accurate performance measurement.
 */
public class PreciseTimer {
    public PreciseTimer() {
        this("GENERIC_TIMER_NAME");
    }
    private static final double INVERSE_TO_MS = 1e-6;
    private static final double INVERSE_TO_S = 1e-9;
    public PreciseTimer(String name) {
        this.name = name;
        this.time = System.nanoTime();
    }

    private String name;
    public PreciseTimer name(String name) {
        this.name = name;
        return this;
    }

    private long time;
    public double time() { return (System.nanoTime() - time); }
    public double timeMs() { return (System.nanoTime() - time) / 1e6; }
    public double timeS() { return (System.nanoTime() - time) / 1e9; }

    /**
     * Start or restart the timer.
     *
     * @return This timer for method chaining
     */
    public PreciseTimer start() {
        time = System.nanoTime();
        return this;
    }
    /**
     * Start or restart the timer.
     * @return This timer for method chaining
     */
    public PreciseTimer restart() {
        time = System.nanoTime();
        return this;
    }

    /**
     * Get the elapsed time since the timer was started.
     *
     * @return TimeSpec representing the elapsed duration
     */
//    public TimeSpec time() {
//        return new TimeSpec(System.nanoTime() - time);
//    }

    /**
     * Log the current elapsed time to telemetry.
     *
     * @param unit Time unit for display (default: milliseconds)
     */
    public void log(Logger log, TimeUnit unit) {
        double multiplier = 1;
        if (unit == TimeUnit.MILLIS) multiplier = INVERSE_TO_MS;
        else if (unit == TimeUnit.SECONDS) multiplier = INVERSE_TO_S;
        log.write("Timer: " + name + " -- [" + unit + "]", (System.nanoTime() - time) * multiplier);
    }

    public void log(Logger log) {
        log.write("Timer: " + name + " -- [MILLIS]", (System.nanoTime() - time) * INVERSE_TO_MS);
    }
}