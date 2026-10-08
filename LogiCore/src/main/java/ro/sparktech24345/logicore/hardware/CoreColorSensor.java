package ro.sparktech24345.logicore.hardware;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.utils.TickInterval;

/**
 * Color sensor with throttled updates and RGBA color extraction.
 * Provides individual color channel access with update rate control.
 */
public class CoreColorSensor implements CoreModule {
    public CoreColorSensor(String name) {
        this(name, 3);
    }

    private final String name;

    public CoreColorSensor(String name, double interval) {
        this.name = name;
        this.ticker = new TickInterval(interval);
    }

    private ColorSensor sensor;
    private final TickInterval ticker;
    public TickInterval getTicker() { return this.ticker; }
    private volatile int color = 0;

    /** Red color channel (0-255) */
    private volatile int r = 0;

    /** Green color channel (0-255) */
    private volatile int g = 0;

    /** Blue color channel (0-255) */
    private volatile int b = 0;

    /** Alpha/opacity channel (0-255) */
    private volatile int a = 0;

    public void initCore() {
        sensor = CoreOpMode.getInstance().hardwareMap.get(ColorSensor.class, name);
    }

    public void loopCore() {}

    /**
     * Update color readings at throttled rate.
     * Extracts individual RGBA channels from the ARGB integer value.
     */
    public void readCore() {
        if (!ticker.shouldTick()) return;
        CoreOpMode.schedule(() -> {
            color = sensor.argb();
            a = (color >> 24) & 0xFF;
            r = (color >> 16) & 0xFF;
            g = (color >> 8) & 0xFF;
            b = color & 0xFF;
        });
    }
}
