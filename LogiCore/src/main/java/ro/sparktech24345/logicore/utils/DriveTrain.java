package ro.sparktech24345.logicore.utils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import dev.frozenmilk.dairy.cachinghardware.CachingDcMotorEx;
import ro.sparktech24345.logicore.config.Keys;
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

    private int rfk = 0;
    private int rbk = 0;
    private int lbk = 0;
    private int lfk = 0;

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
        rfk = Keys.key(rf.getController().getConnectionInfo(), rf.getPortNumber());
        rbk = Keys.key(rb.getController().getConnectionInfo(), rb.getPortNumber());
        lbk = Keys.key(lb.getController().getConnectionInfo(), lb.getPortNumber());
        lfk = Keys.key(lf.getController().getConnectionInfo(), lf.getPortNumber());
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
        });
    }

    public void writePowers() {
        System.out.printf(
            "x=%.3f y=%.3f turn=%.3f powers=[%.3f, %.3f, %.3f, %.3f]%n",
            gamepad.left_stick_x, gamepad.left_stick_y, gamepad.right_stick_x,
            rfp, lfp, lbp, rbp
        );
    }
    private final CachingTracker rft = new CachingTracker(.02);
    private final CachingTracker rbt = new CachingTracker(.02);
    private final CachingTracker lbt = new CachingTracker(.02);
    private final CachingTracker lft = new CachingTracker(.02);

    public void writeCore() {
        Benchmark.of("drivetrain", () -> {
            if (CoreOpMode.instance().config().type.get() == OpModeType.AUTONOMOUS) return;
            // Apply slowdown multiplier and set motor powers

            //CoreOpMode.instance!!.coreTelemetry.addData("Motor rf", rf.portNumber)
            //CoreOpMode.instance!!.coreTelemetry.addData("Motor rb", rb.portNumber)
            //CoreOpMode.instance!!.coreTelemetry.addData("Motor lf", lf.portNumber)
            //CoreOpMode.instance!!.coreTelemetry.addData("Motor lb", lb.portNumber)

            if (rft.shouldUpdate(rfp)) Benchmark.of(rfn, () -> CoreOpMode.instance().setMotorPower(rfk, rf, rfp));
            if (rbt.shouldUpdate(rbp)) Benchmark.of(rbn, () -> CoreOpMode.instance().setMotorPower(rbk, rb, rbp));
            if (lbt.shouldUpdate(lbp)) Benchmark.of(lbn, () -> CoreOpMode.instance().setMotorPower(lbk, lb, lbp));
            if (lft.shouldUpdate(lfp)) Benchmark.of(lfn, () -> CoreOpMode.instance().setMotorPower(lfk, lf, lfp));
        });
    }
}