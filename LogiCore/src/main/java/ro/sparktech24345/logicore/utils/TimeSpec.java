package ro.sparktech24345.logicore.utils;

/**
 * Time specification with flexible unit conversion.
 * Stores time in nanoseconds internally but provides convenient access methods.
 */
public class TimeSpec implements Comparable<TimeSpec> {
    public TimeSpec() {
        this(System.nanoTime());
    }
    public TimeSpec(double timeNano) {
        this.timeNano = timeNano;
    }
    public TimeSpec(long timeNano) {
        this.timeNano = (double)timeNano;
    }

    private final double timeNano;

    public int compareTo(TimeSpec other) {
        return Double.compare(timeNano, other.timeNano);
    }

    public static TimeSpec fromNano(long timeNano) {
        return new TimeSpec(timeNano);
    }

    public static TimeSpec fromMillis(double timeMillis) {
        return new TimeSpec((long)Math.floor(timeMillis * 1e6));
    }

    public static TimeSpec fromSeconds(double timeSec) {
        return fromMillis(timeSec * 1e3);
    }

    /**
     * Get time value in specified unit.
     * @param unit Time unit (NANOS, MILLIS, SECONDS)
     * @return Time value in requested unit, or null if unit not supported
     */
    public double get(TimeUnit unit) {
        switch (unit) {
            case NANOS: return getNs();
            case MILLIS: return getMs();
            case SECONDS: return getSec();
            default: return 0;
        }
    }

    public double getNs() {
        return (double)timeNano;
    }

    public double getMs() {
        return timeNano / 1.0e6;
    }

    public double getSec() {
        return getMs() / 1.0e3;
    }
}