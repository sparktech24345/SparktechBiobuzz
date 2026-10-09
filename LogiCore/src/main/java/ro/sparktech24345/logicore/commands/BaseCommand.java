package ro.sparktech24345.logicore.commands;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import ro.sparktech24345.logicore.core.CoreQueuer;
import ro.sparktech24345.logicore.states.HasStates;
import ro.sparktech24345.logicore.states.StateSet;
import ro.sparktech24345.logicore.utils.TimeSpec;

/**
 * Base class for all commands in the LogiCore command system.
 * Implements a state machine with start/execute/finish lifecycle and conditional execution.
 *
 */
public class BaseCommand {

    protected Runnable command;

    public BaseCommand(Runnable command) {
        this.command = command;
    }

    /** Whether the command has started (startCondition met) */
    protected boolean started = false;
    public boolean started() { return this.started; }

    /** Whether the command has finished (finishCondition met) */
    protected boolean finished = false;
    public boolean finished() { return this.finished; }

    /** Condition that must be true for the command to start */
    protected BooleanSupplier startCondition = () -> true;
    public void startCondition(BooleanSupplier sup) { this.startCondition = sup; }
    public BooleanSupplier startCondition() { return this.startCondition; }

    /** Condition that must be true for the command to finish */
    protected BooleanSupplier finishCondition = () -> true;
    public void finishCondition(BooleanSupplier sup) { this.finishCondition = sup; }
    public BooleanSupplier finishCondition() { return this.finishCondition; }

    /** Action to execute when the command starts */
    protected Runnable onStart = () -> {};
    public void onStart(Runnable fun) { this.onStart = fun; }
    public Runnable onStart() { return this.onStart; }

    /** Action to execute when the command finishes */
    protected Runnable onFinish = () -> {};
    public void onFinish(Runnable fun) { this.onFinish = fun; }
    public Runnable onFinish() { return this.onFinish; }

    /** Name for debugging and telemetry purposes */
    private String name = "GENERIC_ACTION_NAME";
    public void name(String name) { this.name = name; }
    public String name() { return this.name; }

    /**
     * Update command state machine.
     * Handles start condition checking, command execution, and finish condition checking.
     */
    public void update() {
        if (!started) {
            started = startCondition.getAsBoolean();
            if (started) onStart.run();
        }
        if (!finished && started) {
            command.run();
            finished = finishCondition.getAsBoolean();
            if (finished) onFinish.run();
        }
    }

    /**
     * Reset command state for reuse.
     * Called automatically when command is removed from queues.
     */
    public void cleanup() {
        started = false;
        finished = false;
    }

    public static <Dt, St extends StateSet<Dt>> StateCommand<Dt, St> command(HasStates<Dt, St> stateComponent, St state) {
        return new StateCommand<>(stateComponent, state);
    }
    public static DelayCommand command(TimeSpec t) {
        return new DelayCommand(t);
    }

    public static SequenceCommand command(Consumer<CoreQueuer> c) {
        return new SequenceCommand(c);
    }

    public static BaseCommand command(Runnable r) {
        return new BaseCommand(r);
    }
}