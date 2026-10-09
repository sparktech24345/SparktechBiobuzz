package ro.sparktech24345.logicore.hardware;

import com.qualcomm.robotcore.hardware.ColorSensor;

import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.utils.MathUtils;
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
    public TickInterval ticker() { return this.ticker; }
    private volatile int color = 0;
    public int color() { return color; }

    /** Red color channel (0-255) */
    private volatile int r = 0;
    public int r() { return r; }

    /** Green color channel (0-255) */
    private volatile int g = 0;
    public int g() { return g; }

    /** Blue color channel (0-255) */
    private volatile int b = 0;
    public int b() { return b; }

    /** Alpha/opacity channel (0-255) */
    private volatile int a = 0;
    public int a() { return a; }

    /** hue channel (0-1) */
    private volatile double h = 0;
    public double h() { return h; }

    /** saturation channel (0-1) */
    private volatile double s = 0;
    public double s() { return s; }

    /** value channel (0-1) */
    private volatile double v = 0;
    public double v() { return v; }


    public void initCore() {
        sensor = CoreOpMode.instance().hardwareMap.get(ColorSensor.class, name);
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
            double rp = r * inverse;
            double gp = g * inverse;
            double bp = b * inverse;
            double max = MathUtils.max(rp, gp, bp);
            double min = MathUtils.min(rp, gp, bp);
            double delta = max - min;
            v = max;
            s = max > 0 ? delta / max : 0;
            if (delta == .0) h = 0;
            else if (max == rp) h = 60.0 * ((gp - bp) / delta);
            else if (max == gp) h = 60.0 * (((bp - rp) / delta) + 2.0);
            else h = 60.0 * (((rp - gp) / delta) + 4.0);
        });
    }
    private static final double inverse = 1.0 / 255.0;
}
