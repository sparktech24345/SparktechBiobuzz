package ro.sparktech24345.logicore.hardware;

import android.util.Pair;

import com.qualcomm.robotcore.hardware.ServoImplEx;

import ro.sparktech24345.logicore.config.IsHardware;
import ro.sparktech24345.logicore.config.Keys;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.states.HasStates;
import ro.sparktech24345.logicore.states.StateSet;
import ro.sparktech24345.logicore.utils.TickInterval;

/**
 * Enhanced servo control with position mapping and state management.
 * Supports custom position ranges and automatic servo position clamping.
 */
public class CoreServo<T extends StateSet<Double>> implements CoreModule, HasStates<Double, T>, IsHardware {
    public CoreServo(String name, T initialState) {
        this(name, initialState, 0);
    }

    public CoreServo(String name, T initialState, double threshold) {
        this(name, initialState, threshold, 1);
    }

    public CoreServo(String name, T initialState, double threshold, double interval) {
        this.name = name;
        this.states = (Class<T>) initialState.getClass();
        state(initialState);
        this.ticker = new TickInterval(interval);
        this.threshold = threshold;
    }

    private final String name;
    private final TickInterval ticker;

    public TickInterval ticker() {
        return this.ticker;
    }

    private CachingServo servo;

    /** The servo, wrapped in a CachingServo (only changed positions are sent). */
    public CachingServo servo() {
        return this.servo;
    }

    /** Minimum position change that gets sent (passed to the CachingServo). */
    private double threshold;

    public double threshold() {
        return this.threshold;
    }

    public void threshold(double value) {
        this.threshold = value;
        if (servo != null) servo.threshold(value);
    }

    private final Class<T> states;

    /**
     * Set servo to a specific state position
     */
    public void state(T state) {
        this.realPosition = state.value();
        this.currState = state;
    }

    public Class<T> states() {
        return this.states;
    }

    private T currState;

    @Override
    public T state() {
        return currState;
    }

    /**
     * Internal servo position
     */
    private double realPosition = 0;

    /**
     * External position in user-defined units
     */
    public double position() {
        return this.realPosition * rangeDif + range.first;
    }

    public void position(double value) {
        this.realPosition = (value - range.first) / rangeDif;
    }

    /**
     * Position range in user-defined units (auto-normalized if invalid)
     */
    private Pair<Double, Double> range = new Pair<>(0.0, 1.0);

    public void range(Pair<Double, Double> value) {
        if (value.second.equals(value.first))
            throw new IllegalArgumentException("Range [" + value.first + ", " + value.second + "] is invalid.");
        range = value.second < value.first ? new Pair<>(value.second, value.first) : value;
        rangeDif = range.second - range.first;
    }

    public Pair<Double, Double> range() {
        return this.range;
    }

    /**
     * Calculated range difference for position mapping
     */
    private double rangeDif = 1;

    public void initCore() {
        servo = new CachingServo(CoreOpMode.instance().hardwareMap.get(ServoImplEx.class, name), threshold);
        key = Keys.key(servo.getController().getConnectionInfo(), servo.getPortNumber());
    }

    public void loopCore() {
    }

    /**
     * Update servo position every loop cycle (CachingServo skips unchanged positions)
     */
    public void writeCore() {
        if (!ticker.shouldTick()) return;
        servo.setPosition(realPosition);
    }

    private int key;

    @Override
    public void key(int key) {
        this.key = key;
    }

    @Override
    public int key() {
        return key;
    }
}
