package ro.sparktech24345.logicore.utils;

import ro.sparktech24345.logicore.core.CoreOpMode;

public class CachingTracker {
    public CachingTracker(double threshold) {
        this(threshold, 0);
    }
    public CachingTracker(double threshold, double lastValue) {
        this.threshold = threshold;
        this.lastValue = lastValue;
    }

    private double threshold;
    public void threshold(double value) {
        this.threshold = value;
    }
    public double threshold() {
        return this.threshold;
    }
    private double lastValue;

    public boolean shouldUpdate(double newValue) {
        boolean condition = MathUtils.abs(lastValue - newValue) > threshold ||
                (newValue == 0 && lastValue != 0) ||
                (newValue >= 1.0 && lastValue < 1.0) ||
                (newValue <= -1.0 && lastValue > -1.0) ||
                Double.isNaN(lastValue);
        if (condition) this.lastValue = newValue;
        return condition;
    }
}
