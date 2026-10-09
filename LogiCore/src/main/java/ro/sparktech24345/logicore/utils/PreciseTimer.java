package ro.sparktech24345.logicore.utils;

import ro.sparktech24345.logicore.core.CoreTelemetry;

/**
 * High-precision timer for benchmarking and timing operations.
 * Uses nanosecond precision for accurate performance measurement.
 */
public class PreciseTimer {
    public PreciseTimer() {
        this("GENERIC_TIMER_NAME");
    }
    public PreciseTimer(String name) {
        this.name = name;
        this.time = System.nanoTime();
    }

    private final String name;

    private long time;

    /**
     * Start or restart the timer.
     * @return This timer for method chaining
     */
    public PreciseTimer start() {
        time = System.nanoTime();
        return this;
    }

    /**
     * Get the elapsed time since the timer was started.
     * @return TimeSpec representing the elapsed duration
     */
    public TimeSpec getTime() {
        return new TimeSpec(System.nanoTime() - time);
    }

    /**
     * Log the current elapsed time to telemetry.
     * @param telemetry Optional telemetry object (null-safe)
     * @param unit Time unit for display (default: milliseconds)
     */
    public void log(CoreTelemetry telemetry, TimeUnit unit) {
        String caption = "Timer: " + name + " -- [ms]";
        double time = getTime().get(unit);
        if (telemetry != null) telemetry.addData(caption, time);
//        System.out.println(caption + " " + time);
    }

    public void log(CoreTelemetry telemetry) {
        this.log(telemetry, TimeUnit.MILLIS);
    }
}