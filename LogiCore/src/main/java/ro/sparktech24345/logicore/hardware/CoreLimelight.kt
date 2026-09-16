package ro.sparktech24345.logicore.hardware

import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.LLStatus
import com.qualcomm.hardware.limelightvision.Limelight3A
import ro.sparktech24345.logicore.core.CoreModule
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.events.Event
import ro.sparktech24345.logicore.events.EventBus
import ro.sparktech24345.logicore.utils.TickInterval

/**
 * Limelight 3A vision sensor integration with throttled updates.
 * Provides pipeline switching and valid result tracking for vision processing.
 *
 * @param name Hardware device name from the robot configuration
 * @param interval Update interval in ticks (default: 1, updates every loop cycle)
 */
class CoreLimelight(val name: String, interval: Double = 1.0) : CoreModule {
    class LimelightResultEvent(result: LLResult): Event()
    val tracker = TickInterval(interval)

    lateinit var limelight: Limelight3A

    /** Current vision pipeline (0-based index) */
    var pipeline: Int = 0
        set(value) {
            field = value
            limelight.pipelineSwitch(value)
        }

    /** Limelight device status information */
    var status: LLStatus? = null

    /** Latest valid vision result (null if no valid result available) */
    var result: LLResult? = null

    override fun init() {
        limelight = CoreOpMode.instance!!.hardwareMap[name] as Limelight3A
        limelight.pipelineSwitch(pipeline)
        limelight.start()
        status = limelight.status
    }

    /**
     * Update vision results at throttled rate.
     * Only stores results that pass the validity check.
     */
    override fun loop() {
        if (!tracker.shouldTick()) return
        val tempResult = limelight.latestResult
        if (tempResult.isValid) {
            result = tempResult
            EventBus.emit(LimelightResultEvent(tempResult))
        }
    }

    /** Stop the Limelight vision processing */
    override fun stop() {
        limelight.stop()
    }
}
