package ro.sparktech24345.logicore.hardware;

import android.util.Pair;
import com.qualcomm.robotcore.hardware.Servo;
import dev.frozenmilk.dairy.cachinghardware.CachingServo;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;
import ro.sparktech24345.logicore.states.HasStates;
import ro.sparktech24345.logicore.utils.TickInterval;

/**
 * Enhanced servo control with position mapping and state management.
 * Supports custom position ranges and automatic servo position clamping.
 */
public class CoreServo<T extends BaseStateSet<Double>> implements CoreModule, HasStates<Double, T> {
    public CoreServo(String name, T stateSet) {
        this(name, stateSet, 1);
    }
    public CoreServo(String name, T stateSet, double interval) {
        this.name = name;
        this.states = stateSet;
        this.ticker = new TickInterval(interval);
    }
    private final String name;
    private final TickInterval ticker;
    public TickInterval getTicker() { return this.ticker; }
    private CachingServo servo;
    public CachingServo servo() { return this.servo; }

    private final T states;

    /** Set servo to a specific state position */
    public <S extends CoreState<Double>> void setState(S state) {
        this.realPosition = state.getValue();
    }

    public T getStates() { return this.states; }

    /** Internal servo position */
    private double realPosition = 0;

    /** External position in user-defined units */
    public double getPosition() { return this.realPosition * rangeDif + range.first; }
    public void setPosition(double value) {
            this.realPosition = (value - range.first) / rangeDif;
        }

    /** Position range in user-defined units (auto-normalized if invalid) */
    private Pair<Double, Double> range = new Pair<>(0.0, 1.0);
        public void setRange(Pair<Double, Double> value) {
            if (value.second.equals(value.first)) throw new IllegalArgumentException("Range [" + value.first + ", " + value.second + "] is invalid.");
            range = value.second < value.first ? new Pair<>(value.second, value.first) : value;
            rangeDif = range.second - range.first;
        }

    /** Calculated range difference for position mapping */
    private double rangeDif = 1;

    public void initCore() {
        states.own(this);
        servo = new CachingServo(CoreOpMode.getInstance().hardwareMap.get(Servo.class, name));
    }

    public void loopCore() {}

    /** Update servo position every loop cycle */
    public void writeCore() {
        if (!ticker.shouldTick()) return;
        servo.setPosition(realPosition);
    }
}
