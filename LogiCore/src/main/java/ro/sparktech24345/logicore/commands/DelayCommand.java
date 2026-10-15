package ro.sparktech24345.logicore.commands;

import ro.sparktech24345.logicore.utils.PreciseTimer;
import ro.sparktech24345.logicore.utils.TimeSpec;
import ro.sparktech24345.logicore.utils.TimeUnit;

/**
 * Command that waits for a specified duration before finishing.
 * Useful for creating timing delays in autonomous sequences.
 *
 */
public class DelayCommand extends BaseCommand {
    public DelayCommand(TimeSpec time) {
        super(() -> {
        });
        this.onStart = () -> timer.start();
        this.finishCondition = () -> timer.time() >= time.getNs();
    }

    protected PreciseTimer timer = new PreciseTimer();
}