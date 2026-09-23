package ro.sparktech24345.logicore.core

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ro.sparktech24345.logicore.utils.Benchmark
import ro.sparktech24345.logicore.utils.TickInterval
import kotlin.concurrent.Volatile

/**
 * Enhanced telemetry system with update throttling and multi-output support.
 * Extends MultipleTelemetry to support both FTC SDK and FTC Dashboard output simultaneously.
 *
 * @param interval Update interval in ticks (default: 3, updates every 3rd loop cycle)
 */

class CoreTelemetry(interval: Double = 1.0): MultipleTelemetry(), CoreModule {
    /** Tick interval tracker for throttling telemetry updates */
    val tracker = TickInterval(interval)
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun initCore() = Unit
    override fun loopCore() = Unit

    /**
     * Update telemetry only when the tick interval allows.
     * This prevents excessive CPU usage from frequent telemetry updates.
     */
    override fun writeCore() = Benchmark.of("telemetry") {
        if (tracker.shouldTick()) scope.launch {
            update()
        }
    }
}
