package ro.sparktech24345.logicore.utils;

import ro.sparktech24345.logicore.core.CoreOpMode;

public class Benchmark {
    /** Set from OpModeConfig.debug in CoreOpMode.initCore. Off by default: timing every block costs loop time. */
    public static volatile boolean enabled = false;

    /**
     * Benchmark a block of code when debug mode is enabled.
     * Logs execution time to both telemetry and console.
     */
    public static void of(String name, Runnable run) {
        if (!enabled) {
            run.run();
            return;
        }
        Benchmark.of(name, CoreOpMode.instance().logger(), run);
    }

    public static void of(String name, Logger log, Runnable run) {
        if (!enabled || log == null) {
            run.run();
            return;
        }
        PreciseTimer bm = new PreciseTimer(name).start();
        run.run();
        bm.log(log);
    }
}