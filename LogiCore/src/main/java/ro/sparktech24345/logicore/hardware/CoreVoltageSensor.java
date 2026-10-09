package ro.sparktech24345.logicore.hardware;

import com.qualcomm.robotcore.hardware.VoltageSensor;

import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.utils.Benchmark;
import ro.sparktech24345.logicore.utils.TickInterval;

/**
 * Battery voltage monitoring with throttled updates.
 * Automatically detects the first available voltage sensor on the robot controller.
 */
public class CoreVoltageSensor implements CoreModule {
    public CoreVoltageSensor() {
        this(3);
    }

    public CoreVoltageSensor(double interval) {
        this.ticker = new TickInterval(interval);
    }

    private VoltageSensor sensor;
    private final TickInterval ticker;

    public TickInterval ticker() {
        return this.ticker;
    }

    /**
     * Current battery voltage in volts
     */
    private volatile double voltage = 0;

    public double voltage() {
        return this.voltage;
    }

    public void initCore() {
        sensor = CoreOpMode.instance().hardwareMap.getAll(VoltageSensor.class).iterator().next();
    }

    public void loopCore() {
    }

    /**
     * Update voltage reading at throttled rate
     */
    public void readCore() {
        Benchmark.of("voltage sensor", () -> {
            if (ticker.shouldTick()) CoreOpMode.schedule(() -> voltage = sensor.getVoltage());
        });
    }

}