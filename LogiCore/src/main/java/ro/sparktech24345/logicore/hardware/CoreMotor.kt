package ro.sparktech24345.logicore.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorImplEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import dev.frozenmilk.dairy.cachinghardware.CachingDcMotorEx
import ro.sparktech24345.logicore.core.CoreModule
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.states.BaseStateSet
import ro.sparktech24345.logicore.states.CoreState
import ro.sparktech24345.logicore.states.HasStates
import ro.sparktech24345.logicore.utils.TickInterval

/**
 * Enhanced motor control with state management, PID control, and multiple run modes.
 * Supports power, position, velocity, and custom control modes with encoder integration.
 *
 * @param T The type of state set this motor uses
 * @param name Hardware device name from the robot configuration
 * @param stateSet State definitions for this motor
 */
class CoreMotor<T : BaseStateSet>(val name: String, stateSet: T, interval: Double = 1.0) : CoreModule,
    HasStates<T> {

    lateinit var motor: CachingDcMotorEx
        private set

    override val states: T = stateSet

    val tracker = TickInterval(interval)

    /**
     * Set the motor to a specific state.
     * Automatically handles run mode switching based on state type.
     */
    override fun <T : CoreState> setState(state: T) {
        target = state.value
    }

    private var target: Double = 0.0

    private var wantedPower: Double = 0.0

    /** Whether to update this motor during init_loop stage */
    var updateInInit = false

    /** Whether this motor has an encoder installed */
    var encoded = false
        set(value) {
            if (field != value) encoderChange = true
            field = value
        }

    private var encoderChange = false

    /** Encoder ticks per revolution (auto-detected if possible) */
    var unitsPerRev: Double = 1.0

    /** Current encoder position in ticks */
    var currentPosition: Double = Double.NaN
        private set

    /** Motor behavior when power is set to 0 */
    var zeroPowerBehavior: DcMotor.ZeroPowerBehavior = DcMotor.ZeroPowerBehavior.UNKNOWN
        set(value) {
            if (field != value) behaviorChange = true
            field = value
        }
    private var behaviorChange = false

    var direction = DcMotorSimple.Direction.FORWARD
        set(value) {
            if (field != value) directionChanged = true
            field = value
        }
    private var directionChanged = false

    companion object {
        private val DEFAULT_LOOP: (CoreMotor<*>, Double) -> Double = { _, t -> t }
    }

    /** Custom control loop function for advanced motor control */
    var customLoop: (CoreMotor<T>, Double) -> Double = DEFAULT_LOOP

    fun resetCustomLoop() {
        customLoop = DEFAULT_LOOP
    }

    /** Reverse motor direction */
    fun reverse(enabled: Boolean = true) {
        motor.direction = if (enabled) DcMotorSimple.Direction.REVERSE else DcMotorSimple.Direction.FORWARD
    }

    /** Enable/disable encoder functionality */
    fun encoder(enabled: Boolean = true) {
        encoded = enabled
    }

    override fun initCore() {
        states.own(this)
        motor = CachingDcMotorEx(CoreOpMode.instance!!.hardwareMap[name] as DcMotorImplEx)
        unitsPerRev =
            (motor.dcMotorEx as DcMotorImplEx?)?.controller?.getMotorType(motor.portNumber)?.ticksPerRev ?: Double.NaN
    }

    override fun init_loopCore() {
        if (updateInInit) loopCore()
    }

    override fun readCore() {
        if (zeroPowerBehavior != motor.zeroPowerBehavior &&
            zeroPowerBehavior != DcMotor.ZeroPowerBehavior.UNKNOWN)
            motor.zeroPowerBehavior = zeroPowerBehavior
        if (direction != motor.direction)
            motor.direction = direction
        if (encoded != (motor.mode == DcMotor.RunMode.RUN_USING_ENCODER)) {
            motor.mode = if (encoded) DcMotor.RunMode.RUN_USING_ENCODER
                         else DcMotor.RunMode.RUN_WITHOUT_ENCODER
        }
    }

    /** Calculates the wanted power to be sent to the motor */
    override fun loopCore() {
        wantedPower = customLoop(this, target)
    }

    override fun writeCore() {
        if (!tracker.shouldTick()) return
        motor.power = wantedPower.coerceIn(-1.0, 1.0)
    }
}
