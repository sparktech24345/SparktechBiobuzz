package ro.sparktech24345.logicore.core;

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.Arrays;

import ro.sparktech24345.logicore.utils.Benchmark;
import ro.sparktech24345.logicore.utils.TickInterval;

/**
 * Enhanced telemetry system with update throttling and multi-output support.
 * Extends MultipleTelemetry to support both FTC SDK and FTC Dashboard output simultaneously.
 */

public class CoreTelemetry implements CoreModule {
    public MultipleTelemetry tel;
    public CoreTelemetry(double interval, double delay, Telemetry... telemetryList) {
        tel = new MultipleTelemetry(telemetryList);
        this.ticker = new TickInterval(interval < 1 ? 1 : interval, delay < 0 ? 0 : delay);
    }
    public CoreTelemetry(Telemetry... telemetryList) {
        this(1, 0, telemetryList);
    }

    /** Tick interval tracker for throttling telemetry updates */
    private final TickInterval ticker;
    public TickInterval getTicker() { return this.ticker; }
    public void initCore() {
    }
    public void loopCore() {}

    /**
     * Update telemetry only when the tick interval allows.
     * This prevents excessive CPU usage from frequent telemetry updates.
     */
    public void writeCore() {
        if (ticker.shouldTick()) {
            tel.update();
        }
    }
}
