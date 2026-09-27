package ro.sparktech24345.logicore.commands;

import ro.sparktech24345.logicore.utils.PreciseTimer;
import ro.sparktech24345.logicore.utils.TimeSpec;

/**
 * Command that waits for a specified duration before finishing.
 * Useful for creating timing delays in autonomous sequences.
 *
 */
public class DelayCommand extends BaseCommand {
    public DelayCommand(TimeSpec time) {
        super(() -> {});
        this.onStart = () -> { timer.start(); };
        this.finishCondition = () -> timer.getTime().compareTo(time) >= 0;
    }
    protected PreciseTimer timer = new PreciseTimer();
}