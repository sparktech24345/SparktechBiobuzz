package ro.sparktech24345.logicore.hardware

import com.qualcomm.robotcore.hardware.ColorSensor
import ro.sparktech24345.logicore.core.CoreModule
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.utils.TickInterval

/**
 * Color sensor with throttled updates and RGBA color extraction.
 * Provides individual color channel access with update rate control.
 *
 * @param name Hardware device name from the robot configuration
 * @param interval Update interval in ticks (default: 3, updates every 3rd loop cycle)
 */
class CoreColorSensor(val name: String, interval: Double = 3.0) : CoreModule {
    lateinit var sensor: ColorSensor
    val tracker = TickInterval(interval)
    private var color: UInt = 0u

    /** Red color channel (0-255) */
    var r: UInt = 0u

    /** Green color channel (0-255) */
    var g: UInt = 0u

    /** Blue color channel (0-255) */
    var b: UInt = 0u

    /** Alpha/opacity channel (0-255) */
    var a: UInt = 0u

    override fun init() {
        sensor = CoreOpMode.instance!!.hardwareMap[name] as ColorSensor
    }

    override fun init_loop() = loop()

    /**
     * Update color readings at throttled rate.
     * Extracts individual RGBA channels from the ARGB integer value.
     */
    override fun loop() {
        if (!tracker.shouldTick()) return
        color = sensor.argb().toUInt()
        a = (color shr 24) and 0xFFu
        r = (color shr 16) and 0xFFu
        g = (color shr 8) and 0xFFu
        b = color and 0xFFu
    }
}
