package ro.sparktech24345.logicore.utils

import ro.sparktech24345.logicore.core.CoreTelemetry
import java.time.temporal.ChronoUnit

/**
 * High-precision timer for benchmarking and timing operations.
 * Uses nanosecond precision for accurate performance measurement.
 *
 * @param name Identifier for the timer (useful for debugging/telemetry)
 */
class PreciseTimer(val name: String = "GENERIC_TIMER_NAME") {

    enum class TimeUnit {
        NANOS,
        MILLIS,
        SECONDS
    }

    /**
     * Time specification with flexible unit conversion.
     * Stores time in nanoseconds internally but provides convenient access methods.
     */
    class TimeSpec(private val timeNano: Long = System.nanoTime()): Comparable<TimeSpec> {

        override fun compareTo(other: TimeSpec) = timeNano.compareTo(other.timeNano)

        companion object {
            fun fromNano(timeNano: Long): TimeSpec {
                return TimeSpec(timeNano)
            }

            fun fromMillis(timeMillis: Double): TimeSpec {
                return TimeSpec((timeMillis * 1e6).toLong())
            }

            fun fromSeconds(timeSec: Double): TimeSpec {
                return fromMillis(timeSec * 1e3)
            }
        }

        /**
         * Get time value in specified unit.
         * @param unit Time unit (NANOS, MILLIS, SECONDS)
         * @return Time value in requested unit, or null if unit not supported
         */
        fun get(unit: TimeUnit = TimeUnit.MILLIS): Double {
            return when (unit) {
                TimeUnit.NANOS -> getNs()
                TimeUnit.MILLIS -> getMs()
                TimeUnit.SECONDS -> getSec()
            }
        }

        fun getNs(): Double {
            return timeNano.toDouble()
        }

        fun getMs(): Double {
            return timeNano / 1.0e6
        }

        fun getSec(): Double {
            return getMs() / 1.0e3
        }
    }

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
        telemetry?.addLine("Timer: $name -- ${getTime().get(unit) ?: 0} ms")
    }
}