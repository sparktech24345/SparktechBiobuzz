package ro.sparktech24345.logicore.hardware

import com.qualcomm.robotcore.hardware.Servo
import dev.frozenmilk.dairy.cachinghardware.CachingServo
import ro.sparktech24345.logicore.core.CoreModule
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.states.BaseStateSet
import ro.sparktech24345.logicore.states.CoreState
import ro.sparktech24345.logicore.states.HasStates

/**
 * Enhanced servo control with position mapping and state management.
 * Supports custom position ranges and automatic servo position clamping.
 *
 * @param T The type of state set this servo uses
 * @param name Hardware device name from the robot configuration
 * @param stateSet State definitions for this servo
 */
class CoreServo<T : BaseStateSet>(val name: String, stateSet: T) : CoreModule,
    HasStates<T> {

    lateinit var servo: CachingServo

    override val states = stateSet

    /** Set servo to a specific state position */
    override fun <T : CoreState> setState(state: T) {
        position = state.value
    }

    /** Internal servo position (0.0 to 1.0) with safety clamping */
    var realPosition: Double = 0.0
        set(value) {
            field = value.coerceIn(0.0..1.0)
        }

    /** External position in user-defined units */
    var position: Double
        get() = realPosition * rangeDif + range.first
        set(value) {
            realPosition = (value - range.first) / rangeDif
        }

    /** Position range in user-defined units (auto-normalized if invalid) */
    var range: Pair<Double, Double> = Pair(0.0, 1.0)
        set(value) {
            if (value.second == value.first) error("Unable to set range of [${value.first}, ${value.second}] because it has a difference of 0. Please input a valid range!")
            field = if (value.second < value.first) Pair(value.second, value.first) else value
            rangeDif = field.second - field.first
        }

    /** Calculated range difference for position mapping */
    var rangeDif = 1.0

    override fun init() {
        states.own(this)
        servo = CachingServo(CoreOpMode.instance!!.hardwareMap[name] as Servo)
        realPosition = servo.position
    }

    override fun init_loop() = loop()

    /** Update servo position every loop cycle */
    override fun loop() {
        servo.position = realPosition
    }
}
