package ro.sparktech24345.logicore.pedro;

import com.pedropathing.paths.Path;

import java.util.function.BooleanSupplier;

import ro.sparktech24345.logicore.commands.BaseCommand;

public class FollowCommand extends BaseCommand {
    public FollowCommand(CoreFollower<?> follower, Path path, BooleanSupplier finishCond) {
        super(() -> {
        });
        this.onStart = () -> follower.follow(path);
        this.finishCondition = finishCond;
    }

    public FollowCommand(CoreFollower<?> follower, Path path) {
        this(follower, path, () -> true);
    }

}