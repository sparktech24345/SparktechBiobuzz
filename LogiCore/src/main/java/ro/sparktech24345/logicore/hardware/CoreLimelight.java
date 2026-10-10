package ro.sparktech24345.logicore.hardware;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.events.Event;
import ro.sparktech24345.logicore.events.EventBus;
import ro.sparktech24345.logicore.utils.TickInterval;

/**
 * Limelight 3A vision sensor integration with throttled updates.
 * Provides pipeline switching and valid result tracking for vision processing.
 */
public class CoreLimelight implements CoreModule {

    public CoreLimelight(String name) {
        this(name, 1);
    }

    public CoreLimelight(String name, double interval) {
        this.name = name;
        this.ticker = new TickInterval(interval);
    }

    private final String name;

    public static class LimelightResultEvent extends Event {
        public LimelightResultEvent(LLResult result) {
            this.result = result;
        }

        private final LLResult result;

        public LLResult result() {
            return this.result;
        }
    }

    private final TickInterval ticker;

    public TickInterval ticker() {
        return this.ticker;
    }

    private Limelight3A limelight = null;

    /**
     * Current vision pipeline (0-based index)
     */
    private int pipeline = 0;

    public void pipeline(int pipeline) {
        this.pipeline = pipeline;
        if (limelight != null) limelight.pipelineSwitch(pipeline);
    }

    public int pipeline() {
        return this.pipeline;
    }

    /**
     * Limelight device status information
     */
    private LLStatus status = null;

    public LLStatus status() {
        return this.status;
    }

    /**
     * Latest valid vision result (null if no valid result available)
     */
    private LLResult result = null;

    public LLResult result() {
        return this.result;
    }

    public void initCore() {
        limelight = CoreOpMode.instance().hardwareMap.get(Limelight3A.class, name);
        limelight.pipelineSwitch(pipeline);
        limelight.start();
        status = limelight.getStatus();
    }

    public void loopCore() {
    }

    /**
     * Update vision results at throttled rate.
     * Only stores results that pass the validity check.
     */
    public void readCore() {
        if (!ticker.shouldTick()) return;
        LLResult tempResult = limelight.getLatestResult();
        if (tempResult.isValid()) {
            result = tempResult;
            EventBus.emit(new LimelightResultEvent(tempResult));
        }
    }

    /**
     * Stop the Limelight vision processing
     */
    public void stopCore() {
        limelight.stop();
    }
}
