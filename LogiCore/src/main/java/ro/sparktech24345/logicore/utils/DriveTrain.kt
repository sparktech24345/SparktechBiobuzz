package ro.sparktech24345.logicore.utils

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import dev.anygeneric.blazeftc.BlazeFTC
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

    override fun initCore() {
        val map = CoreOpMode.instance!!.hardwareMap
        rf = CachingDcMotorEx(map[rightFront] as DcMotorEx, 0.05)
        lf = CachingDcMotorEx(map[leftFront]  as DcMotorEx, 0.05)
        rb = CachingDcMotorEx(map[rightBack]  as DcMotorEx, 0.05)
        lb = CachingDcMotorEx(map[leftBack]   as DcMotorEx, 0.05)

        lf.direction = DcMotorSimple.Direction.REVERSE
        lb.direction = DcMotorSimple.Direction.REVERSE
        this.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    private var rfp = 0.0
    private var rbp = 0.0
    private var lfp = 0.0
    private var lbp = 0.0

    /**
     * Update drive train with mecanum drive calculations.
     * Implements holonomic drive with automatic power normalization.
     */
    override fun loopCore() = Benchmark.of("drivetrain calc") {
        if (CoreOpMode.instance!!.config.type.get() == CoreOpMode.OpModeType.AUTONOMOUS) return@of
        var vertical   = -gamepad.left_stick_y.toDouble()
        var horizontal = -gamepad.left_stick_x.toDouble()
        val pivot      =  gamepad.right_stick_x.toDouble()

        if (directionFlip) {
            horizontal *= -1
            vertical *= -1
        }

        // Mecanum drive calculations
        rfp = vertical + horizontal - pivot
        rbp = vertical - horizontal - pivot
        lfp = vertical - horizontal + pivot
        lbp = vertical + horizontal + pivot

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
    }

    override fun writeCore() = Benchmark.of("drivetrain") {
        if (CoreOpMode.instance!!.config.type.get() == CoreOpMode.OpModeType.AUTONOMOUS) return@of
        // Apply slowdown multiplier and set motor powers

        //CoreOpMode.instance!!.coreTelemetry.addData("Motor rf", rf.portNumber)
        //CoreOpMode.instance!!.coreTelemetry.addData("Motor rb", rb.portNumber)
        //CoreOpMode.instance!!.coreTelemetry.addData("Motor lf", lf.portNumber)
        //CoreOpMode.instance!!.coreTelemetry.addData("Motor lb", lb.portNumber)

        if (CoreOpMode.instance!!.config.performanceEngine.get() == CoreOpMode.PerformanceEngine.BLAZE) {
            BlazeFTC.setMotorPower(173, rf.portNumber, rfp * slowdownMultiplier * (if (rf.direction == DcMotorSimple.Direction.REVERSE) -1 else 1))
            BlazeFTC.setMotorPower(173,lf.portNumber, lfp * slowdownMultiplier * (if (lf.direction == DcMotorSimple.Direction.REVERSE) -1 else 1))
            BlazeFTC.setMotorPower(173, lb.portNumber, lbp * slowdownMultiplier * (if (lb.direction == DcMotorSimple.Direction.REVERSE) -1 else 1))
            BlazeFTC.setMotorPower(173, rb.portNumber, rbp * slowdownMultiplier * (if (rb.direction == DcMotorSimple.Direction.REVERSE) -1 else 1))
        } else {
            rf.power = rfp * slowdownMultiplier
            rb.power = rbp * slowdownMultiplier
            lf.power = lfp * slowdownMultiplier
            lb.power = lbp * slowdownMultiplier
        }
    }
}