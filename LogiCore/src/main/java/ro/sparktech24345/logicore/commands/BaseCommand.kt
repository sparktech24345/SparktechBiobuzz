package ro.sparktech24345.logicore.commands

/**
 * Base class for all commands in the LogiCore command system.
 * Implements a state machine with start/execute/finish lifecycle and conditional execution.
 *
 * @param command The main action to execute while the command is running
 */
open class BaseCommand(open var command: () -> Unit = {}) {
    /** Whether the command has started (startCondition met) */
    var started = false
        private set

    /** Whether the command has finished (finishCondition met) */
    var finished = false
        private set

    /** Condition that must be true for the command to start */
    open var startCondition: () -> Boolean = { true }

    /** Condition that must be true for the command to finish */
    open var finishCondition: () -> Boolean = { true }

    /** Action to execute when the command starts */
    open var onStart: () -> Unit = {}

    /** Action to execute when the command finishes */
    open var onFinish: () -> Unit = {}

    /** Name for debugging and telemetry purposes */
    var name: String = "GENERIC_ACTION_NAME"

    /**
     * Update command state machine.
     * Handles start condition checking, command execution, and finish condition checking.
     */
    fun update() {
        if (!started) {
            started = startCondition()
            if (started) onStart()
        }
        if (!finished && started) {
            command()
            finished = finishCondition()
            if (finished) onFinish()
        }
    }

    /**
     * Reset command state for reuse.
     * Called automatically when command is removed from queues.
     */
    open fun cleanup() {
        started = false
        finished = false
    }
}
