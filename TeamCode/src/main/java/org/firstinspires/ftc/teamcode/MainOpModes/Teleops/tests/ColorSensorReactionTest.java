package org.firstinspires.ftc.teamcode.MainOpModes.Teleops.tests;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.photon.PhotonCore;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Config
@TeleOp(name = "ColorSensorReactionTest", group = "tests")
public class ColorSensorReactionTest extends LinearOpMode {
    public static double servoPos = 0.88;
    public static double motorPowe = 0;

    /// ----------------- Color Sensor Stuff ------------------
    protected NormalizedColorSensor colorSensorRight;
    protected NormalizedColorSensor colorSensorLeft;
    protected volatile NormalizedRGBA rightSensorColors;
    protected volatile NormalizedRGBA leftSensorColors;

    public static int ballCounter = 0;
    protected BallColorSet_Decode actualRightSensorDetectedBall;
    protected BallColorSet_Decode calculatedRightSensorDetectedBall;
    protected BallColorSet_Decode actualLeftSensorDetectedBall;
    protected BallColorSet_Decode calculatedLeftSensorDetectedBall;
    private ScheduledExecutorService colorSensorExecutor;
    private ExecutorService telExecutor;


    // Place these instance variables in your class:
    private long lastFrameTimeNanos = 0;
    private double maxLoopMls = 0;
    private double minLoopMls = Double.MAX_VALUE;
    private double totalLoopMls = 0;
    private int loopCount = 0;
    volatile boolean shouldMoveCameraServo = false;
    @Override
    public void runOpMode() throws InterruptedException {
        // Bulk caching setup
        PhotonCore.CONTROL_HUB.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        PhotonCore.EXPANSION_HUB.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        PhotonCore.experimental.setMaximumParallelCommands(6); // Can be adjusted based on user preference - but raising this number further can cause issues

        // REMOVED setMaximumParallelCommands(8) to prevent RS-485 serial timeouts / packet drop spikes
        PhotonCore.PARALLELIZE_SERVOS = true;
        PhotonCore.enable();

        Telemetry tel = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        ElapsedTime telTimer = new ElapsedTime();

        /// servo time
        Servo CameraRotateServo = hardwareMap.get(Servo.class, "CameraRotateServo");

        colorSensorRight = hardwareMap.get(NormalizedColorSensor.class, "colorSensorRight");
        colorSensorLeft = hardwareMap.get(NormalizedColorSensor.class, "colorSensorLeft");

        telExecutor = Executors.newSingleThreadExecutor();
        colorSensorExecutor = Executors.newSingleThreadScheduledExecutor();

// Inside runOpMode():

        waitForStart();
        if (isStopRequested()) return;

// Start background color sensor reads (using fixed delay to prevent task piling)
        colorSensorExecutor.scheduleWithFixedDelay(() -> {
            if (opModeIsActive()) {
                if (colorSensorRight != null) {
                    rightSensorColors = colorSensorRight.getNormalizedColors();
                }
                if (colorSensorLeft != null) {
                    leftSensorColors = colorSensorLeft.getNormalizedColors();
                }
            }
            shouldMoveCameraServo =
                    BallColorSet_Decode.getColorForStorage(rightSensorColors) != BallColorSet_Decode.NoBall
                    || BallColorSet_Decode.getColorForStorage(leftSensorColors,true) != BallColorSet_Decode.NoBall;
        }, 0, 3, TimeUnit.MILLISECONDS);

        lastFrameTimeNanos = System.nanoTime();
        telTimer.reset();

        while (opModeIsActive()) {
            PhotonCore.CONTROL_HUB.clearBulkCache();
            PhotonCore.EXPANSION_HUB.clearBulkCache();


            double voltaj = PhotonCore.CONTROL_HUB.getInputVoltage(VoltageUnit.VOLTS);

            // --- ACCURATE LOOP MEASUREMENT (Frame-Start to Frame-Start) ---
            long currentNanos = System.nanoTime();
            double currentLoopMls = (currentNanos - lastFrameTimeNanos) / 1_000_000.0;
            lastFrameTimeNanos = currentNanos;

            // Accumulate metrics
            maxLoopMls = Math.max(maxLoopMls, currentLoopMls);
            minLoopMls = Math.min(minLoopMls, currentLoopMls);
            totalLoopMls += currentLoopMls;
            loopCount++;
            if (CameraRotateServo != null)
                if(shouldMoveCameraServo)CameraRotateServo.setPosition(0.6);
            else CameraRotateServo.setPosition(0.5);

            // --- TELEMETRY AGGREGATION (Capped at ~30 Hz / 33 ms) ---
            if (telTimer.milliseconds() >= 33) {
                double elapsedTelMls = telTimer.milliseconds();

                // Take snapshots of window statistics
                double snapshotMax = maxLoopMls;
                double snapshotMin = minLoopMls;
                double snapshotAvg = totalLoopMls / Math.max(1, loopCount);
                double actualHz = loopCount * (1000.0 / elapsedTelMls);
                int snapshotLoopCount = loopCount;

                NormalizedRGBA rightColors = rightSensorColors;
                NormalizedRGBA leftColors = leftSensorColors;

                // Offload serialization to background thread safely
                telExecutor.submit(() -> {
                    tel.addData("Actual Hz (Loops/Sec)", "%.1f", actualHz);
                    tel.addData("Avg Loop Time (ms)", "%.2f", snapshotAvg);
                    tel.addData("Min Loop Time (ms)", "%.2f", snapshotMin);
                    tel.addData("Max Loop Spike (ms)", "%.2f", snapshotMax);
                    tel.addData("Loops in 33ms Window", snapshotLoopCount);

                    if (rightColors != null) {
                        tel.addData("Right Red", "%.3f", rightColors.red);
                    }
                    if (leftColors != null) {
                        tel.addData("Left Red", "%.3f", leftColors.red);
                    }
                    tel.update();
                });

                // Reset accumulation window
                maxLoopMls = 0;
                minLoopMls = Double.MAX_VALUE;
                totalLoopMls = 0;
                loopCount = 0;
                telTimer.reset();
            }
        }

        // Clean up thread pools on stop
        colorSensorExecutor.shutdownNow();
        telExecutor.shutdownNow();
    }
}