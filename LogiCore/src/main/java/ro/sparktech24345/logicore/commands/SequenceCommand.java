package ro.sparktech24345.logicore.commands;

import java.util.function.Consumer;

import ro.sparktech24345.logicore.core.CoreQueuer;

/**
 * Command that executes a sequence of other commands.
 * The provided function can queue multiple commands which will be executed sequentially.
 */
public class SequenceCommand extends BaseCommand {
    public SequenceCommand(Consumer<CoreQueuer> run) {
        super(() -> {
        });
        this.onStart = () -> run.accept(queuer);
        this.command = queuer::loopCore;
        this.finishCondition = () -> !queuer.busy();
        this.onFinish = queuer::clear;
    }

    private final CoreQueuer queuer = new CoreQueuer();
}

