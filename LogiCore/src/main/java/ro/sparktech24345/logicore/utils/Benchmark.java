package ro.sparktech24345.logicore.utils;

import ro.sparktech24345.logicore.core.CoreOpMode;

public class Benchmark {
    /** Set from OpModeConfig.debug in CoreOpMode.initCore. Off by default: timing every block costs loop time. */
    public static volatile boolean enabled = false;
    private static final int MAX_BENCHES = 100;
    private static final PreciseTimer[] timers = new PreciseTimer[MAX_BENCHES];
    private static int stackPtr = 0;

    /**
     * Benchmark a block of code when debug mode is enabled.
     * Logs execution time to both telemetry and console.
     */

    static {
        for (int i = 0; i < MAX_BENCHES; ++i) timers[i] = new PreciseTimer("default stuff");
    }

    public static void init() {
        stackPtr = 0;
    }

    public static void start(String name) {
        if (!enabled) return;
        if (stackPtr >= MAX_BENCHES) return;
        timers[stackPtr++].name(name).start();
    }

    public static void end() {
        if (!enabled) return;
        if (stackPtr <= 0) return;
        timers[--stackPtr].log(CoreOpMode.instance().logger());
    }
}