package ro.sparktech24345.logicore.utils

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import dev.frozenmilk.dairy.cachinghardware.CachingDcMotorEx
import ro.sparktech24345.logicore.core.CoreModule
import ro.sparktech24345.logicore.core.CoreOpMode

/**
 * Mecanum drive train implementation with gamepad control.
 * Provides holonomic movement with strafing, rotation, and direction control.
 *
 * @param gamepad Gamepad for driver input
 * @param rightFront Hardware name for right front motor
 * @param leftFront Hardware name for left front motor
 * @param rightBack Hardware name for right back motor
 * @param leftBack Hardware name for left back motor
 */
class DriveTrain (
    val gamepad: Gamepad,
    val rightFront: String = "frontright",
    val leftFront: String = "frontleft",
    val rightBack: String = "backright",
    val leftBack: String = "backleft",
) : CoreModule {
    private lateinit var rf: CachingDcMotorEx
    private lateinit var lf: CachingDcMotorEx
    private lateinit var rb: CachingDcMotorEx
    private lateinit var lb: CachingDcMotorEx

    /** Zero power behavior for all motors (applied to all motors when set) */
    var zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        set(value) {
            field = value
            rf.zeroPowerBehavior = field
            lf.zeroPowerBehavior = field
            rb.zeroPowerBehavior = field
            lb.zeroPowerBehavior = field
        }

    /** Reverse the driving direction (useful for driving from different orientations) */
    var directionFlip = false

    /** Speed multiplier for fine control (0.0 to 1.0) */
    var slowdownMultiplier = 1.0
        set(value) { field = value.coerceIn(0.0..1.0) }

    override fun init() {
        val map = CoreOpMode.instance!!.hardwareMap
        rf = CachingDcMotorEx(map[rightFront] as DcMotorEx)
        lf = CachingDcMotorEx(map[leftFront]  as DcMotorEx)
        rb = CachingDcMotorEx(map[rightBack]  as DcMotorEx)
        lb = CachingDcMotorEx(map[leftBack]   as DcMotorEx)

        lf.direction = DcMotorSimple.Direction.REVERSE
        lb.direction = DcMotorSimple.Direction.REVERSE
        this.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    /**
     * Update drive train with mecanum drive calculations.
     * Implements holonomic drive with automatic power normalization.
     */
    override fun loop() {
        var vertical   = -gamepad.left_stick_y.toDouble()
        var horizontal = -gamepad.left_stick_x.toDouble()
        val pivot      =  gamepad.right_stick_x.toDouble()

        if (directionFlip) {
            horizontal *= -1
            vertical *= -1
        }

        // Mecanum drive calculations
        var rfp = vertical + horizontal - pivot
        var rbp = vertical - horizontal - pivot
        var lfp = vertical - horizontal + pivot
        var lbp = vertical + horizontal + pivot

        // Normalize power to prevent saturation
        val div = MathUtils.max(
            MathUtils.abs(rfp),
            MathUtils.abs(rbp),
            MathUtils.abs(lfp),
            MathUtils.abs(lbp)
        )

        if (div > 1.0) {
            rfp /= div
            rbp /= div
            lfp /= div
            lbp /= div
        }

        // Apply slowdown multiplier and set motor powers
        rf.power = (rfp * slowdownMultiplier)
        rb.power = rbp * slowdownMultiplier
        lf.power = lfp * slowdownMultiplier
        lb.power = lbp * slowdownMultiplier
    }
}