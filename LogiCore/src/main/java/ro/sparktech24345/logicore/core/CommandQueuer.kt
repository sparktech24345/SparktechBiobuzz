package ro.sparktech24345.logicore.core

import ro.sparktech24345.logicore.commands.BaseCommand

/**
 * Interface for command scheduling and execution systems.
 * Provides sequential command execution with queuing capabilities.
 */
interface CommandQueuer : CoreModule {
    /** Add a command to the execution queue */
    fun queue(command: BaseCommand)

    /** Execute a command immediately (bypasses queue) */
    fun execute(command: BaseCommand)

    /** Clear all pending and executing commands */
    fun clear()
}
