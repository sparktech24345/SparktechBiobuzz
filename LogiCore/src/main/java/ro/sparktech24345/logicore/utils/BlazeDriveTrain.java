package ro.sparktech24345.logicore.utils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import dev.anygeneric.blazeftc.AcceleratedMotor;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.core.OpModeType;

/**
 * Mecanum drive train implementation for blaze with gamepad control.
 * Provides holonomic movement with strafing, rotation, and direction control.
 */
public class BlazeDriveTrain extends DriveTrain {
    public BlazeDriveTrain(Gamepad gp) {
        super(gp, "frontright", "frontleft", "backright", "backleft");
    }
    public BlazeDriveTrain(Gamepad gp, String rightFront, String leftFront, String rightBack, String leftBack) {
        super(gp, rightFront, leftFront, rightBack, leftBack);
    }
    private AcceleratedMotor rf;
    private AcceleratedMotor lf;
    private AcceleratedMotor rb;
    private AcceleratedMotor lb;

    @Override
    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior value) {
        this.zeroPowerBehavior = value;
        if (rf != null) rf.setZeroPowerBehavior(value);
        if (lf != null) lf.setZeroPowerBehavior(value);
        if (rb != null) rb.setZeroPowerBehavior(value);
        if (lb != null) lb.setZeroPowerBehavior(value);
    }

    @Override
    public void initCore() {
        HardwareMap map = CoreOpMode.getInstance().hardwareMap;
        rf = CoreOpMode.getMotor(map, rfn);
        lf = CoreOpMode.getMotor(map, lfn);
        rb = CoreOpMode.getMotor(map, rbn);
        lb = CoreOpMode.getMotor(map, lbn);

        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lb.setDirection(DcMotorSimple.Direction.REVERSE);
        this.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void writeCore() {
        Benchmark.of("drivetrain blaze", () -> {
            if (CoreOpMode.getInstance().getConfig().type.get() == OpModeType.AUTONOMOUS) return;
            // Apply slowdown multiplier and set motor powers

            //CoreOpMode.instance!!.coreTelemetry.addData("Motor rf", rf.portNumber)
            //CoreOpMode.instance!!.coreTelemetry.addData("Motor rb", rb.portNumber)
            //CoreOpMode.instance!!.coreTelemetry.addData("Motor lf", lf.portNumber)
            //CoreOpMode.instance!!.coreTelemetry.addData("Motor lb", lb.portNumber)
            rf.setPower(rfp * slowdownMultiplier);
            rb.setPower(rbp * slowdownMultiplier);
            lf.setPower(lfp * slowdownMultiplier);
            lb.setPower(lbp * slowdownMultiplier);
        });
    }
}
