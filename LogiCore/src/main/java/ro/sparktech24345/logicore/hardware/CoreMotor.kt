package ro.sparktech24345.logicore.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorImplEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import dev.frozenmilk.dairy.cachinghardware.CachingDcMotorEx
import ro.sparktech24345.logicore.core.CoreModule
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.states.BaseStateSet
import ro.sparktech24345.logicore.states.CoreMotorState
import ro.sparktech24345.logicore.states.CoreState
import ro.sparktech24345.logicore.states.HasStates
import kotlin.math.floor

/**
 * Enhanced motor control with state management, PID control, and multiple run modes.
 * Supports power, position, velocity, and custom control modes with encoder integration.
 *
 * @param T The type of state set this motor uses
 * @param name Hardware device name from the robot configuration
 * @param stateSet State definitions for this motor
 */
class CoreMotor<T : BaseStateSet>(val name: String, stateSet: T) : CoreModule,
    HasStates<T> {

    lateinit var motor: CachingDcMotorEx
        private set

    override val states: T = stateSet

    /**
     * Set the motor to a specific state.
     * Automatically handles run mode switching based on state type.
     */
    override fun <T : CoreState> setState(state: T) {
        if (state is CoreMotorState) runMode = state.runMode
        when (runMode) {
            MotorRunMode.POWER -> power = state.value
            MotorRunMode.POSITION -> position = state.value
            MotorRunMode.VELOCITY -> velocity = state.value
            MotorRunMode.CUSTOM -> customTarget = state.value
        }
    }

    /** Whether to update this motor during init_loop stage */
    var updateInInit = false

    /** Whether this motor has an encoder installed */
    var encoded = false

    /** Encoder ticks per revolution (auto-detected if possible) */
    var unitsPerRev: Double = 1.0
        private set

    /** Motor power control (-1.0 to 1.0) */
    var power: Double
        set(value) {
            runMode = MotorRunMode.POWER
            motor.mode = motorRunMode()
            motor.power = value
        }
        get() = motor.power

    /** Current encoder position in ticks */
    val currentPosition: Double
        get() = motor.currentPosition.toDouble()

    /** Power to use when in position mode */
    var positionPower: Double = 1.0
        set(value) {
            field = value
            if (runMode == MotorRunMode.POSITION) motor.power = field
        }

    /** Target position in encoder ticks */
    var position: Double
        set(value) {
            if (!encoded) error("Unable for motor to run on position since it is not flagged as having an encoder!")
            runMode = MotorRunMode.POSITION
            motor.mode = motorRunMode()
            motor.targetPosition = floor(value).toInt()
        }
        get() = motor.targetPosition.toDouble()

    /** Target velocity in ticks per second */
    var velocity: Double
        set(value) {
            if (!encoded) error("Unable for motor to run on velocity since it is not flagged as having an encoder!")
            runMode = MotorRunMode.VELOCITY
            motor.mode = motorRunMode()
            motor.velocity = value
        }
        get() = motor.velocity

    /** Motor behavior when power is set to 0 */
    var zeroPowerBehavior: DcMotor.ZeroPowerBehavior
        set(value) {
            motor.zeroPowerBehavior = value
        }
        get() = motor.zeroPowerBehavior

    /** Available motor control modes */
    enum class MotorRunMode {
        POWER,      // Direct power control
        POSITION,   // Position control with encoder
        VELOCITY,   // Velocity control with encoder
        CUSTOM      // Custom control loop
    }

    /** Target value for custom control mode */
    var customTarget: Double = 0.0

    /** Whether custom control mode requires encoder */
    var customLoopRequiresEncoder = false

    /** Custom control loop function for advanced motor control */
    var customLoop: (DcMotorEx, Double) -> Unit = { _, _ -> }
        set(value) {
            field = value
            runMode = MotorRunMode.CUSTOM
        }

    /** Current motor run mode with validation and automatic configuration */
    var runMode = MotorRunMode.POWER
        set(value) {
            field = value
            if (!encoded && (field == MotorRunMode.POSITION || field == MotorRunMode.VELOCITY ||
                        (field == MotorRunMode.CUSTOM && customLoopRequiresEncoder))
            ) {
                error("Cannot run on position, velocity, or custom (if requires encoder) without encoder flag!")
            }
            if (value == MotorRunMode.POSITION) motor.power = positionPower
            motor.mode = motorRunMode(value)
            if (encoded && field != MotorRunMode.CUSTOM) motor.setPIDFCoefficients(motor.mode, pidf)
        }

    /** Convert internal run mode to FTC SDK run mode */
    private fun motorRunMode(runMode: MotorRunMode = this.runMode): RunMode = when (runMode) {
        MotorRunMode.POWER -> RunMode.RUN_WITHOUT_ENCODER
        MotorRunMode.POSITION -> RunMode.RUN_TO_POSITION
        else -> RunMode.RUN_USING_ENCODER
    }

    /** Reverse motor direction */
    fun reverse(enabled: Boolean = true) {
        motor.direction = if (enabled) DcMotorSimple.Direction.REVERSE else DcMotorSimple.Direction.FORWARD
    }

    /** Enable/disable encoder functionality */
    fun encoder(enabled: Boolean = true) {
        encoded = enabled
    }

    /** Enable/disable motor updates during init_loop */
    fun updateDuringInit(enabled: Boolean = true) {
        updateInInit = enabled
    }

    /** PIDF coefficients for position/velocity control */
    var pidf: PIDFCoefficients = PIDFCoefficients(0.0, 0.0, 0.0, 0.0)
        set(value) {
            motor.setPIDFCoefficients(motorRunMode(), value)
            field = value
        }

    override fun init() {
        states.own(this)
        motor = CachingDcMotorEx(CoreOpMode.instance!!.hardwareMap[name] as DcMotorEx)
        unitsPerRev =
            (motor.dcMotorEx as DcMotorImplEx?)?.controller?.getMotorType(motor.portNumber)?.ticksPerRev ?: 1.0
    }

    override fun init_loop() {
        if (updateInInit) loop()
    }

    /** Update motor control - runs custom loop if in CUSTOM mode */
    override fun loop() {
        if (runMode == MotorRunMode.CUSTOM) customLoop(motor, customTarget)
    }
}
