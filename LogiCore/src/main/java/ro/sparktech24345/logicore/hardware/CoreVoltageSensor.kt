package ro.sparktech24345.logicore.hardware

import com.qualcomm.robotcore.hardware.VoltageSensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ro.sparktech24345.logicore.core.CoreModule
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.utils.Benchmark
import ro.sparktech24345.logicore.utils.TickInterval
import kotlin.concurrent.Volatile

/**
 * Battery voltage monitoring with throttled updates.
 * Automatically detects the first available voltage sensor on the robot controller.
 *
 * @param interval Update interval in ticks (default: 5, updates every 5th loop cycle)
 */
class CoreVoltageSensor(interval: Double = 3.0) : CoreModule {
    private lateinit var sensor: VoltageSensor
    val tracker = TickInterval(interval)
    companion object {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    /** Current battery voltage in volts */
    @Volatile
    var voltage: Double = 0.0
        private set

    override fun initCore() {
        sensor =
            CoreOpMode.instance!!.hardwareMap.getAll(VoltageSensor::class.java).iterator().next()
    }

    override fun loopCore() = Unit

    /** Update voltage reading at throttled rate */
    override fun readCore() = Benchmark.of("voltage sensor") {
        if (tracker.shouldTick()) scope.launch { voltage = sensor.voltage }
    }

}