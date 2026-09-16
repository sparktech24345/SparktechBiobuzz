package ro.sparktech24345.logicore.commands

import ro.sparktech24345.logicore.core.CoreQueuer

/**
 * Command that executes a sequence of other commands.
 * The provided function can queue multiple commands which will be executed sequentially.
 *
 * @param run Function that queues commands into the provided CoreQueuer
 */
class SequenceCommand(private val run: (CoreQueuer) -> Unit) : BaseCommand() {
    private val queuer = CoreQueuer()

    /** Queue the sequence of commands when this command starts */
    override var onStart = { run(queuer) }

    /** Process the queued commands each update cycle */
    override var command = { queuer.loop() }

    /** Command finishes when all queued commands have completed */
    override var finishCondition = { !queuer.busy }

    /** Clean up the inner queuer when the sequence finishes */
    override var onFinish = { queuer.clear() }
}

