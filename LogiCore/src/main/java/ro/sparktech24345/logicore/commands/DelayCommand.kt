package ro.sparktech24345.logicore.commands

import ro.sparktech24345.logicore.utils.PreciseTimer
import ro.sparktech24345.logicore.utils.PreciseTimer.TimeSpec

/**
 * Command that waits for a specified duration before finishing.
 * Useful for creating timing delays in autonomous sequences.
 *
 * @param time The duration to wait before the command finishes
 */
class DelayCommand(private val time: TimeSpec): BaseCommand() {
    private var timer = PreciseTimer()

    /** Start the timer when the command begins */
    override var onStart: () -> Unit = { timer.start() }

    /** Command finishes when the elapsed time reaches the target duration */
    override var finishCondition = { timer.getTime() >= time }
}