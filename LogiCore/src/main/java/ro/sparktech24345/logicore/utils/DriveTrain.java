package ro.sparktech24345.logicore.utils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import dev.frozenmilk.dairy.cachinghardware.CachingDcMotorEx;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.core.OpModeType;

/**
 * Mecanum drive train implementation with gamepad control.
 * Provides holonomic movement with strafing, rotation, and direction control.
 */
public class DriveTrain implements CoreModule {
    public DriveTrain(Gamepad gp) {
        this(gp, "frontright", "frontleft", "backright", "backleft");
    }

    public DriveTrain(Gamepad gp, String rightFront, String leftFront, String rightBack, String leftBack) {
        this.gamepad = gp;
        this.rfn = rightFront;
        this.lfn = leftFront;
        this.rbn = rightBack;
        this.lbn = leftBack;
    }

    protected final Gamepad gamepad;
    protected final String rbn;
    protected final String lbn;
    protected final String rfn;
    protected final String lfn;

    protected CachingDcMotorEx rf;
    protected CachingDcMotorEx lf;
    protected CachingDcMotorEx rb;
    protected CachingDcMotorEx lb;

    /**
     * Zero power behavior for all motors (applied to all motors when set)
     */
    protected DcMotor.ZeroPowerBehavior zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior value) {
        this.zeroPowerBehavior = value;
        rf.setZeroPowerBehavior(value);
        lf.setZeroPowerBehavior(value);
        rb.setZeroPowerBehavior(value);
        lb.setZeroPowerBehavior(value);
    }

    public DcMotor.ZeroPowerBehavior getZeroPowerBehavior() {
        return this.zeroPowerBehavior;
    }

    /**
     * Reverse the driving direction (useful for driving from different orientations)
     */
    protected boolean directionFlip = false; // ts by default true

    public void setDirectionFlip(boolean v) {
        this.directionFlip = v;
    }

    public boolean getDirectionFlip() {
        return this.directionFlip;
    }

    /**
     * Speed multiplier for fine control (0.0 to 1.0)
     */
    public double slowdownMultiplier = 1.0;

    public void setSlowdownMultiplier(double value) {
        this.slowdownMultiplier = MathUtils.clip(value, 0.0, 1.0);
    }

    public double getSlowdownMultiplier() {
        return this.slowdownMultiplier;
    }

    public void initCore() {
        HardwareMap map = CoreOpMode.instance().hardwareMap;
        DcMotorEx rfm = map.get(DcMotorEx.class, rfn);
        DcMotorEx lfm = map.get(DcMotorEx.class, lfn);
        DcMotorEx rbm = map.get(DcMotorEx.class, rbn);
        DcMotorEx lbm = map.get(DcMotorEx.class, lbn);


        lfm.setDirection(DcMotorSimple.Direction.REVERSE);
        lf = new CachingDcMotorEx(lfm, 0.05);

        rfm.setDirection(DcMotorSimple.Direction.FORWARD);
        rf = new CachingDcMotorEx(rfm, 0.05);

        rbm.setDirection(DcMotorSimple.Direction.FORWARD);
        rb = new CachingDcMotorEx(rbm, 0.05);

        lbm.setDirection(DcMotorSimple.Direction.REVERSE);
        lb = new CachingDcMotorEx(lbm, 0.05);


        this.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    protected double rfp = 0;
    protected double rbp = 0;
    protected double lfp = 0;
    protected double lbp = 0;

    /**
     * Update drive train with mecanum drive calculations.
     * Implements holonomic drive with automatic power normalization.
     */
    public void loopCore() {
        Benchmark.of("drivetrain calc", () -> {
            if (CoreOpMode.instance().config().type.get() == OpModeType.AUTONOMOUS) return;
            double flip = directionFlip ? -1 : 1;
            double vertical = -gamepad.left_stick_y * flip;
            double horizontal = -gamepad.left_stick_x * flip;
            double pivot = -gamepad.right_stick_x;

            // Mecanum drive calculations
            lfp = vertical - horizontal - pivot;
            rfp = vertical + horizontal + pivot;
            lbp = vertical + horizontal - pivot;
            rbp = vertical - horizontal + pivot;

            // Normalize power to prevent saturation
            double div = MathUtils.max(
                    MathUtils.abs(rfp),
                    MathUtils.abs(rbp),
                    MathUtils.abs(lfp),
                    MathUtils.abs(lbp)
            );

            if (div > 1.0) {
                rfp /= div;
                rbp /= div;
                lfp /= div;
                lbp /= div;
            }
            writePowers();
        });
    }

    public void writePowers() {
        System.out.println("Has power in rf:" + rfp);
        System.out.println("Has power in rb:" + rbp);
        System.out.println("Has power in lb:" + lbp);
        System.out.println("Has power in lf:" + lfp);
    }

    public void writeCore() {
        Benchmark.of("drivetrain", () -> {
            if (CoreOpMode.instance().config().type.get() == OpModeType.AUTONOMOUS) return;
            // Apply slowdown multiplier and set motor powers

            //CoreOpMode.instance!!.coreTelemetry.addData("Motor rf", rf.portNumber)
            //CoreOpMode.instance!!.coreTelemetry.addData("Motor rb", rb.portNumber)
            //CoreOpMode.instance!!.coreTelemetry.addData("Motor lf", lf.portNumber)
            //CoreOpMode.instance!!.coreTelemetry.addData("Motor lb", lb.portNumber)

            CoreOpMode.instance().setMotorPower(rf, rfp);
            CoreOpMode.instance().setMotorPower(rb, rbp);
            CoreOpMode.instance().setMotorPower(lb, lbp);
            CoreOpMode.instance().setMotorPower(lf, lfp);
        });
    }
}