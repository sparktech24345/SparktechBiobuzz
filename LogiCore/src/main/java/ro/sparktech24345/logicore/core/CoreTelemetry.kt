package ro.sparktech24345.logicore.core

import ro.sparktech24345.logicore.utils.MultiTelemetry
import ro.sparktech24345.logicore.utils.TickInterval

/**
 * Enhanced telemetry system with update throttling and multi-output support.
 * Extends MultipleTelemetry to support both FTC SDK and FTC Dashboard output simultaneously.
 *
 * @param interval Update interval in ticks (default: 3, updates every 3rd loop cycle)
 */
class CoreTelemetry(interval: Double = 3.0): MultiTelemetry(), CoreModule {
    /** Tick interval tracker for throttling telemetry updates */
    val tracker = TickInterval(interval)

    override fun init() = Unit
    override fun init_loop() = loop()

    /**
     * Update telemetry only when the tick interval allows.
     * This prevents excessive CPU usage from frequent telemetry updates.
     */
    override fun loop() {
        if (tracker.shouldTick()) update()
    }

    override fun setNumDecimalPlaces(minDecimalPlaces: Int, maxDecimalPlaces: Int) {
        super.setNumDecimalPlaces(minDecimalPlaces, maxDecimalPlaces)
    }
}
