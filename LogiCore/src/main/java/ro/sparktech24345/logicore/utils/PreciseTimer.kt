package ro.sparktech24345.logicore.utils

import ro.sparktech24345.logicore.core.CoreTelemetry

/**
 * High-precision timer for benchmarking and timing operations.
 * Uses nanosecond precision for accurate performance measurement.
 *
 * @param name Identifier for the timer (useful for debugging/telemetry)
 */
class PreciseTimer(val name: String = "GENERIC_TIMER_NAME") {

    private var time: Long = System.nanoTime()

    /**
     * Start or restart the timer.
     * @return This timer for method chaining
     */
    fun start(): PreciseTimer {
        time = System.nanoTime()
        return this
    }

    /**
     * Get the elapsed time since the timer was started.
     * @return TimeSpec representing the elapsed duration
     */
    fun getTime(): TimeSpec {
        return TimeSpec(System.nanoTime() - time)
    }

    /**
     * Log the current elapsed time to telemetry.
     * @param telemetry Optional telemetry object (null-safe)
     * @param unit Time unit for display (default: milliseconds)
     */
    fun log(telemetry: CoreTelemetry?, unit: TimeUnit = TimeUnit.MILLIS) {
        telemetry?.addData("Timer: $name -- [ms]:", getTime().get(unit))
    }
}