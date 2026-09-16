package ro.sparktech24345.logicore.hardware

import com.qualcomm.robotcore.hardware.VoltageSensor
import ro.sparktech24345.logicore.core.CoreModule
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.utils.TickInterval

/**
 * Battery voltage monitoring with throttled updates.
 * Automatically detects the first available voltage sensor on the robot controller.
 *
 * @param interval Update interval in ticks (default: 5, updates every 5th loop cycle)
 */
class CoreVoltageSensor(interval: Double = 5.0) : CoreModule {
    private lateinit var sensor: VoltageSensor
    val tracker = TickInterval(interval)

    /** Current battery voltage in volts */
    var voltage: Double = 0.0
        private set

    override fun init() {
        sensor =
            CoreOpMode.instance!!.hardwareMap.getAll(VoltageSensor::class.java).iterator().next()
    }

    override fun init_loop() = loop()

    /** Update voltage reading at throttled rate */
    override fun loop() {
        if (tracker.shouldTick()) voltage = sensor.voltage
    }

}