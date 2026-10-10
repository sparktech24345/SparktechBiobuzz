package ro.sparktech24345.logicore.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ro.sparktech24345.logicore.commands.BaseCommand;

/**
 * Implements command scheduling with two execution modes:
 * - Sequential queue: Commands execute one at a time in order
 * - Parallel executor: Commands execute simultaneously
 */
public class CoreQueuer implements CommandQueuer {
    /**
     * Sequential command queue - executes one command at a time
     */
    private final ArrayDeque<BaseCommand> queuer = new ArrayDeque<>();

    /**
     * Parallel command executor - runs multiple commands simultaneously
     */
    private final List<BaseCommand> executor = new ArrayList<>();

    /**
     * When true, all command processing is paused
     */
    private boolean pause = false;

    public CoreQueuer() {
    }

    public void pause(boolean p) {
        this.pause = p;
    }

    public boolean pause() {
        return this.pause;
    }


    /**
     * True if both queues have pending commands
     */
    public boolean busy() {
        return !queuer.isEmpty() && !executor.isEmpty();
    }

    /**
     * Add a command to the sequential queue
     */
    public void queue(BaseCommand command) {
        queuer.add(command);
    }

    /**
     * Add a command to the parallel executor
     */
    public void execute(BaseCommand command) {
        executor.add(command);
    }

    /**
     * Clean up and remove all commands from both queues
     */
    public void clear() {
        for (BaseCommand q : queuer) q.cleanup();
        queuer.clear();
        for (BaseCommand e : executor) e.cleanup();
        executor.clear();
    }

    public void initCore() {
        loopCore();
    }

    public void init_loopCore() {
        loopCore();
    }

    public void startCore() {
        loopCore();
    }

    /**
     * Update command execution state.
     * Processes sequential queue (one at a time) and parallel executor (simultaneously).
     * Sequential queue only advances when the current command finishes.
     */
    public void loopCore() {
        if (pause) return;

        // Process sequential queue - only one command runs at a time
        Iterator<BaseCommand> iter = queuer.iterator();
        while (iter.hasNext()) {
            BaseCommand command = iter.next();
            command.update();
            if (command.finished()) {
                command.cleanup();
                iter.remove();
            } else break; // Wait for current command to finish
        }

        // Process parallel executor - all commands run simultaneously
        Iterator<BaseCommand> it = executor.iterator();
        while (it.hasNext()) {
            BaseCommand command = it.next();
            command.update();
            if (command.finished()) {
                command.cleanup();
                it.remove();
            }
        }
    }

    public void stopCore() {
        for (BaseCommand command : queuer) command.cleanup();
        for (BaseCommand command : executor) command.cleanup();
        queuer.clear();
        executor.clear();
    }
}
