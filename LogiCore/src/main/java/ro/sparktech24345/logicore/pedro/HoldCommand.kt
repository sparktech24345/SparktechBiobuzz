package ro.sparktech24345.logicore.pedro

import com.pedropathing.math.Pose
import ro.sparktech24345.logicore.commands.BaseCommand
import ro.sparktech24345.logicore.utils.PreciseTimer
import ro.sparktech24345.logicore.utils.PreciseTimer.TimeSpec

class HoldCommand(follower: CoreFollower<*>, pose: Pose, holdTime: TimeSpec = TimeSpec.fromSeconds(0.0), finishCond: () -> Boolean = { true }): BaseCommand() {
    private val timer = PreciseTimer()
    override var onStart: () -> Unit = { follower.hold(pose); if (holdTime.getSec() > 0.0) timer.start() }
    override var finishCondition: () -> Boolean = { (timer.getTime() >= holdTime && holdTime.getSec() > 0.0) || finishCond() }
}