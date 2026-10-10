package ro.sparktech24345.logicore.pedro;

import com.pedropathing.localization.Localizer;
import com.pedropathing.localization.MotionState;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Velocity;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SparkPinpointLocalizer implements Localizer {
    public enum ResetMode {
        RECALIBRATE_IMU,
        RESET_AND_RECALIBRATE_IMU,
        NONE
    }

    private final GoBildaPinpointDriver pinpoint;
    private final DistanceUnit globalDistanceUnit;

    private volatile MotionState motionState;
    private final ResetMode resetMode;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final Object pinpointLock = new Object();

    public SparkPinpointLocalizer(HardwareMap hardwareMap, PinpointConfig config) {
        this.globalDistanceUnit = config.globalDistanceUnit.get();

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, config.name.get());

        pinpoint.setOffsets(config.xPodOffset.get(), config.yPodOffset.get(), config.offsetUnits.get());

        if (config.ticksPerUnit.get().isPresent()) {
            pinpoint.setEncoderResolution(config.ticksPerUnit.get().getAsDouble(), config.encoderResolutionUnit.get());
        } else {
            pinpoint.setEncoderResolution(config.podType.get());
        }

        pinpoint.setEncoderDirections(
                config.xPodDirection.get(),
                config.yPodDirection.get()
        );

        switch (config.resetMode.get()) {
            case RESET_AND_RECALIBRATE_IMU:
                resetMode = ResetMode.RESET_AND_RECALIBRATE_IMU;
                break;

            case RECALIBRATE_IMU:
                resetMode = ResetMode.RECALIBRATE_IMU;
                break;

            case NONE:
                resetMode = ResetMode.NONE;
                break;

            default:
                resetMode = ResetMode.RESET_AND_RECALIBRATE_IMU;
                break;
        }

        reset();

        // Populate initial state synchronously before background execution starts
        synchronized (pinpointLock) {
            readAndStoreState();
        }

        // Start continuous background I2C reading
        executor.submit(this::backgroundLoop);
    }

    private void backgroundLoop() {
        while (isRunning.get() && !Thread.currentThread().isInterrupted()) {
            synchronized (pinpointLock) {
                if (!isRunning.get()) break;
                readAndStoreState();
            }
            try {
                Thread.sleep(7);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void readAndStoreState() {
        pinpoint.update();

        Pose pose = new Pose(
                pinpoint.getPosX(globalDistanceUnit),
                pinpoint.getPosY(globalDistanceUnit),
                pinpoint.getHeading(AngleUnit.RADIANS)
        );

        Velocity velocity = new Velocity(
                pinpoint.getVelX(globalDistanceUnit),
                pinpoint.getVelY(globalDistanceUnit),
                pinpoint.getHeadingVelocity(UnnormalizedAngleUnit.RADIANS)
        );

        motionState = MotionState.ofVelocity(pose, velocity);
    }

    public void setPose(Pose pose) {
        synchronized (pinpointLock) {
            pinpoint.setPosition(
                    new Pose2D(
                            globalDistanceUnit,
                            pose.x(),
                            pose.y(),
                            AngleUnit.RADIANS,
                            pose.heading()
                    )
            );

            if (motionState != null) {
                motionState = motionState.withPose(pose);
            } else {
                motionState = MotionState.ofVelocity(pose, Velocity.zero());
            }
        }
    }

    @Override
    public void update() {
        // No-op for the main thread: motionState is continuously updated asynchronously
        // by the background executor service.
    }

    @Override
    public MotionState state() {
        return motionState;
    }

    public void reset() {
        synchronized (pinpointLock) {
            if (resetMode == ResetMode.RESET_AND_RECALIBRATE_IMU) {
                pinpoint.resetPosAndIMU();
            } else if (resetMode == ResetMode.RECALIBRATE_IMU) {
                pinpoint.recalibrateIMU();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Call this when shutting down or stopping the OpMode to gracefully terminate the I2C thread.
     */
    public void stop() {
        isRunning.set(false);
        executor.shutdownNow();
    }
}