package ro.sparktech24345.logicore.utils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.core.DriveMode;
import ro.sparktech24345.logicore.hardware.CachingMotor;

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

    protected CachingMotor rf;
    protected CachingMotor lf;
    protected CachingMotor rb;
    protected CachingMotor lb;

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
        rf = new CachingMotor(map.get(DcMotorEx.class, rfn));
        lf = new CachingMotor(map.get(DcMotorEx.class, lfn));
        rb = new CachingMotor(map.get(DcMotorEx.class, rbn));
        lb = new CachingMotor(map.get(DcMotorEx.class, lbn));

        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.FORWARD);
        rb.setDirection(DcMotorSimple.Direction.FORWARD);
        lb.setDirection(DcMotorSimple.Direction.REVERSE);



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
            if (!hasControl()) return;
            // CachingMotor only sends powers that changed
            rf.setPower(rfp * slowdownMultiplier);
            rb.setPower(rbp * slowdownMultiplier);
            lb.setPower(lbp * slowdownMultiplier);
            lf.setPower(lfp * slowdownMultiplier);
        });
    }
}