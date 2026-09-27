package ro.sparktech24345.logicore.core;

import ro.sparktech24345.logicore.commands.BaseCommand;

/**
 * Interface for command scheduling and execution systems.
 * Provides sequential command execution with queuing capabilities.
 */
public interface CommandQueuer extends CoreModule {
    /** Add a command to the execution queue */
    void queue(BaseCommand command);

    /** Execute a command immediately (bypasses queue) */
    void execute(BaseCommand command);

    /** Clear all pending and executing commands */
    void clear();
}
