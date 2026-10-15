package ro.sparktech24345.logicore.pedro;

import com.pedropathing.localization.Localizer;
import com.pedropathing.localization.MotionState;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Velocity;
import com.pedropathing.revhub.localizers.OctoQuadConfig;
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad.*;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SparkOctoQuadLocalizer implements Localizer {

    private static final long POLL_INTERVAL_MS = 7;
    private static final long LOCALIZER_TIMEOUT_MS = 5000;

    private final LocalizerDataBlock localizer = new LocalizerDataBlock();
    public final OctoQuad octoQuad;
    private final DistanceUnit globalDistanceUnit;

    private volatile MotionState motionState;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final Object octoQuadLock = new Object();

    public SparkOctoQuadLocalizer(HardwareMap hardwareMap, OctoQuadConfig config) {
        octoQuad = hardwareMap.get(OctoQuad.class, config.name.get());

        globalDistanceUnit = config.globalDistanceUnit.get();

        octoQuad.setSingleEncoderDirection(
                config.xPodPort.get(),
                config.xPodDirection.get()
        );

        octoQuad.setSingleEncoderDirection(
                config.yPodPort.get(),
                config.yPodDirection.get()
        );

        double mmPerUnit = config.encoderResolutionUnit.get().toMm(1.0);
        float ticksPerMM = (float) (config.ticksPerUnit.get() / mmPerUnit);

        octoQuad.setAllLocalizerParameters(
                config.xPodPort.get(),
                config.yPodPort.get(),
                ticksPerMM,
                ticksPerMM,
                (float) -config.offsetUnits.get().toMm(config.xPodOffset.get()),
                (float) -config.offsetUnits.get().toMm(config.yPodOffset.get()),
                config.headingScalar.get().floatValue(),
                config.localizerVelocityIntervalMS.get()
        );

        octoQuad.setI2cRecoveryMode(config.i2cRecoveryMode.get());

        reset();
        waitForLocalizerReady();

        synchronized (octoQuadLock) {
            readAndStoreState();
        }

        executor.submit(this::backgroundLoop);
    }

    private void waitForLocalizerReady() {
        long startTime = System.nanoTime();
        long timeoutNanos = TimeUnit.MILLISECONDS.toNanos(LOCALIZER_TIMEOUT_MS);

        while (true) {
            synchronized (octoQuadLock) {
                if (octoQuad.getLocalizerStatus() == LocalizerStatus.RUNNING) {
                    return;
                }
            }

            if (System.nanoTime() - startTime >= timeoutNanos) {
                throw new IllegalStateException(
                        "OctoQuad localizer did not reach RUNNING status within "
                                + LOCALIZER_TIMEOUT_MS + " ms"
                );
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(
                        "Interrupted while waiting for OctoQuad localizer",
                        e
                );
            }
        }
    }

    private void backgroundLoop() {
        while (isRunning.get() && !Thread.currentThread().isInterrupted()) {
            synchronized (octoQuadLock) {
                if (!isRunning.get()) {
                    break;
                }

                readAndStoreState();
            }

            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void readAndStoreState() {
        octoQuad.readLocalizerData(localizer);

        if (!localizer.isDataValid()) {
            return;
        }

        Pose pose = new Pose(
                globalDistanceUnit.fromMm(localizer.posX_mm),
                globalDistanceUnit.fromMm(localizer.posY_mm),
                localizer.heading_rad
        );

        Velocity velocity = new Velocity(
                globalDistanceUnit.fromMm(localizer.velX_mmS),
                globalDistanceUnit.fromMm(localizer.velY_mmS),
                localizer.velHeading_radS
        );

        motionState = MotionState.ofVelocity(pose, velocity);
    }

    @Override
    public void update() {
        // The background thread continuously updates motionState.
    }

    @Override
    public void setPose(Pose pose) {
        synchronized (octoQuadLock) {
            octoQuad.setLocalizerPose(
                    (int) globalDistanceUnit.toMm(pose.x()),
                    (int) globalDistanceUnit.toMm(pose.y()),
                    (float) pose.heading()
            );

            if (motionState != null) {
                motionState = motionState.withPose(pose);
            } else {
                motionState = MotionState.ofVelocity(pose, Velocity.zero());
            }
        }
    }

    @Override
    public MotionState state() {
        return motionState;
    }

    @Override
    public void reset() {
        synchronized (octoQuadLock) {
            octoQuad.resetLocalizerAndCalibrateIMU();
        }
    }

    public void stop() {
        isRunning.set(false);
        executor.shutdownNow();
    }
}