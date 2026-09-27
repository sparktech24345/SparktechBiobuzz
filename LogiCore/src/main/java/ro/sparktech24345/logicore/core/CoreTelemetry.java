package ro.sparktech24345.logicore.core;

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import ro.sparktech24345.logicore.utils.Benchmark;
import ro.sparktech24345.logicore.utils.TickInterval;

/**
 * Enhanced telemetry system with update throttling and multi-output support.
 * Extends MultipleTelemetry to support both FTC SDK and FTC Dashboard output simultaneously.
 *
 * @param interval Update interval in ticks (default: 3, updates every 3rd loop cycle)
 */

public class CoreTelemetry extends MultipleTelemetry implements CoreModule {

    public CoreTelemetry() {
        this(1, 0);
    }

    public CoreTelemetry(double interval, double delay) {
        super();
        this.ticker = new TickInterval(interval < 1 ? 1 : interval, delay < 0 ? 0 : delay);
    }

    /** Tick interval tracker for throttling telemetry updates */
    private final TickInterval ticker;
    public TickInterval getTicker() { return this.ticker; }
    public void initCore() {}
    public void loopCore() {}

    /**
     * Update telemetry only when the tick interval allows.
     * This prevents excessive CPU usage from frequent telemetry updates.
     */
    public void writeCore() {
        Benchmark.of("telemetry", () -> {
            if (ticker.shouldTick()) CoreOpMode.executor().submit(this::update);
        });
    }
}
