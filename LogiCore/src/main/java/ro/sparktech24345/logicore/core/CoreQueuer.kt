package ro.sparktech24345.logicore.core

import ro.sparktech24345.logicore.commands.BaseCommand

/**
 * Implements command scheduling with two execution modes:
 * - Sequential queue: Commands execute one at a time in order
 * - Parallel executor: Commands execute simultaneously
 */
class CoreQueuer : CommandQueuer {
    /** Sequential command queue - executes one command at a time */
    val queuer: ArrayDeque<BaseCommand> = ArrayDeque()

    /** Parallel command executor - runs multiple commands simultaneously */
    val executor: MutableList<BaseCommand> = mutableListOf()

    /** When true, all command processing is paused */
    var pause = false

    /** True if both queues have pending commands */
    val busy: Boolean
        get() = !queuer.isEmpty() && !executor.isEmpty()

    /** Add a command to the sequential queue */
    override fun queue(command: BaseCommand) {
        queuer += command
    }

    /** Add a command to the parallel executor */
    override fun execute(command: BaseCommand) {
        executor += command
    }

    /** Clean up and remove all commands from both queues */
    override fun clear() {
        for (q in queuer) q.cleanup()
        queuer.clear()
        for (e in executor) e.cleanup()
        executor.clear()
    }

    override fun init() = Unit
    override fun init_loop() = loop()

    /** 
     * Update command execution state.
     * Processes sequential queue (one at a time) and parallel executor (simultaneously).
     * Sequential queue only advances when the current command finishes.
     */
    override fun loop() {
        if (pause) return

        // Process sequential queue - only one command runs at a time
        val iter = queuer.iterator()
        while (iter.hasNext()) {
            val command = iter.next()
            command.update()
            if (command.finished) {
                command.cleanup()
                iter.remove()
            } else break // Wait for current command to finish
        }

        // Process parallel executor - all commands run simultaneously
        val it = executor.iterator()
        while (it.hasNext()) {
            val command = it.next()
            command.update()
            if (command.finished) {
                command.cleanup()
                it.remove()
            }
        }
    }
}
