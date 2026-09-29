package ro.sparktech24345.logicore.utils;

import ro.sparktech24345.logicore.core.CoreOpMode;

public class Benchmark {
    /**
     * Benchmark a block of code when debug mode is enabled.
     * Logs execution time to both telemetry and console.
     */
    public static void of(String name, Runnable run) {
        PreciseTimer bm = new PreciseTimer(name).start();
        run.run();
        bm.log(CoreOpMode.getInstance().getCoreTelemetry());
    }
}