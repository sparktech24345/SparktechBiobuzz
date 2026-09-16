package ro.sparktech24345.logicore.utils


/**
 * Throttling mechanism for operations that don't need to run every loop cycle.
 * Useful for sensor updates, telemetry, and other expensive operations.
 *
 * @param interval Number of loop cycles between updates (default: 1, runs every cycle)
 */
class TickInterval(var interval: Double = 1.0) {
    enum class IntervalMode {
        TICKS,
        TIME,
    }
    private var ticks = 0L
    private var timer = PreciseTimer().start()
    private var firstTick = true

    var mode = IntervalMode.TICKS
    var timeUnit = PreciseTimer.TimeUnit.MILLIS

    /**
     * Check if the current tick should trigger an update.
     * Note: This increments the internal counter, so it should be called exactly once per loop.
     * @return true if an update is due
     */
    fun shouldTick(): Boolean {
        if (firstTick) {
            firstTick = false
            if (mode == IntervalMode.TIME) timer.start()
            return true
        }

        return when (mode) {
            IntervalMode.TICKS -> {
                ticks += 1
                val update = ticks >= interval.toLong()
                if (update) ticks = 0
                update
            }
            IntervalMode.TIME -> {
                val elapsed = timer.getTime().get(timeUnit)
                if (elapsed >= interval) {
                    timer.start()
                    true
                } else false
            }
        }
    }
}