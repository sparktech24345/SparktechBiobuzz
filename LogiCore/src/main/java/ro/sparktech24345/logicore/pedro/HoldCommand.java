package ro.sparktech24345.logicore.pedro;

import com.pedropathing.math.Pose;
import ro.sparktech24345.logicore.commands.BaseCommand;
import ro.sparktech24345.logicore.utils.PreciseTimer;
import ro.sparktech24345.logicore.utils.TimeSpec;

public class HoldCommand extends BaseCommand {
    public HoldCommand(CoreFollower<?> follower, Pose pose, TimeSpec holdTime) {
        super(() -> {});
        this.onStart = () -> { follower.hold(pose); timer.start(); };
        this.finishCondition = () -> timer.getTime().compareTo(holdTime) >= 0;
    }
    private final PreciseTimer timer = new PreciseTimer();
}