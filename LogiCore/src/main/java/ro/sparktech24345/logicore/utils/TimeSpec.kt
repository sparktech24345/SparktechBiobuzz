package ro.sparktech24345.logicore.utils

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