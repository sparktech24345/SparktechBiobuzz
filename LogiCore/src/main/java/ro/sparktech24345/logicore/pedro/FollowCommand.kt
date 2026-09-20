package ro.sparktech24345.logicore.pedro

import com.pedropathing.paths.Path
import ro.sparktech24345.logicore.commands.BaseCommand

class FollowCommand(follower: CoreFollower<*>, path: Path, override var finishCondition: () -> Boolean = { true }): BaseCommand() {
    override var onStart: () -> Unit = { follower.follow(path) }
}