package ro.sparktech24345.logicore.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import dev.anygeneric.blazeftc.AcceleratedMotor;
import dev.anygeneric.blazeftc.BlazeFTC;
import dev.frozenmilk.dairy.cachinghardware.CachingDcMotorEx;
import kotlin.jvm.functions.Function2;
import ro.sparktech24345.logicore.config.ConfigMap;
import ro.sparktech24345.logicore.config.HardwareConfig;
import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.config.IsHardware;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.core.PerformanceEngine;
import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;
import ro.sparktech24345.logicore.states.HasStates;
import ro.sparktech24345.logicore.utils.MathUtils;
import ro.sparktech24345.logicore.utils.TickInterval;

/**
 * Enhanced motor control with state management, PID control, and multiple run modes.
 * Supports power, position, velocity, and custom control modes with encoder integration.
 */
public class CoreMotor<T extends BaseStateSet<Double>> implements CoreModule, HasStates<Double, T>, IsHardware {
    public CoreMotor(String name, T stateSet) {
        this(name, stateSet, 1);
    }

    public CoreMotor(String name, T stateSet, double interval) {
        this.name = name;
        this.states = stateSet;
        this.ticker = new TickInterval(interval);
    }
    private final String name;
    private HardwareConfig config = new HardwareConfig(Hubs.INDEPENDENT, -1);
    public void setConfig(HardwareConfig cfg) { this.config = cfg; }
    public HardwareConfig getConfig() { return this.config; }

    private CachingDcMotorEx motor;
    public CachingDcMotorEx getMotor() { return this.motor; }

    private final T states;

    private final TickInterval ticker;
    public TickInterval getTicker() { return this.ticker; }

    /**
     * Set the motor to a specific state.
     * Automatically handles run mode switching based on state type.
     */
    @Override
    public void setState(CoreState<Double> state) {
        this.target = state.getValue();
        this.currState = state;
    }

    private double target = 0;

    private double wantedPower = 0;

    /** Whether to update this motor during init_loop stage */
    private boolean updateInInit = false;
    public void doUpdatesInInit(boolean value) { this.updateInInit = value; }
    public boolean doesUpdatesInInit() { return this.updateInInit; }

    /** Whether this motor has an encoder installed */
    private boolean encoded = false;
    public void encoded(boolean val) {
        if (encoded != val) encoderChange = true;
        this.encoded = val;
    }
    public boolean encoded() { return this.encoded; }

    private boolean encoderChange = false;

    /** Encoder ticks per revolution (auto-detected if possible) */
    private double unitsPerRev = 1;
    public void setUnitsPerRev(double value) { this.unitsPerRev = value; }
    public double getUnitsPerRev() { return this.unitsPerRev; }

    /** Current encoder position in ticks */
    private double currentPosition = Double.NaN;
    public double currentPosition() { return this.currentPosition; }

    /** Motor behavior when power is set to 0 */
    private DcMotor.ZeroPowerBehavior zeroPowerBehavior = DcMotor.ZeroPowerBehavior.UNKNOWN;

    public void zeroPowerBehavior(DcMotor.ZeroPowerBehavior zpb) {
        if (zeroPowerBehavior != zpb) behaviorChange = true;
        this.zeroPowerBehavior = zpb;
    }
    public DcMotor.ZeroPowerBehavior zeroPowerBehavior() { return this.zeroPowerBehavior; }
    private boolean behaviorChange = false;

    private DcMotorSimple.Direction direction = DcMotorSimple.Direction.FORWARD;
    public void direction(DcMotorSimple.Direction dir) {
            if (dir != direction) directionChanged = true;
            this.direction = dir;
        }
    private boolean directionChanged = false;

    private final Function2<CoreMotor<T>, Double, Double> DEFAULT_LOOP = (m, t) -> t;

    /** Custom control loop function for advanced motor control */
    private Function2<CoreMotor<T>, Double, Double> customLoop = DEFAULT_LOOP;
    public void loop(Function2<CoreMotor<T>, Double, Double> fn) {
        this.customLoop = fn;
    }
    public void resetLoop() {
        this.customLoop = DEFAULT_LOOP;
    }

    /** Reverse motor direction */
    public void reverse() { reverse(true); }
    public void reverse(boolean enabled) {
        motor.setDirection(enabled ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD);
    }

    public void initCore() {
        states.own(this);
        motor = new CachingDcMotorEx(CoreOpMode.getInstance().hardwareMap.get(DcMotorImplEx.class, name));
        unitsPerRev = (motor.getDcMotorEx()).getController().getMotorType(motor.getPortNumber()).getTicksPerRev();
    }

    public void init_loopCore() {
        if (updateInInit) loopCore();
    }

    public void readCore() {
        if (behaviorChange && zeroPowerBehavior != DcMotor.ZeroPowerBehavior.UNKNOWN)
            motor.setZeroPowerBehavior(zeroPowerBehavior);
        if (directionChanged)
            motor.setDirection(direction);
        if (encoderChange)
            motor.setMode(encoded ? DcMotor.RunMode.RUN_USING_ENCODER : DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if (encoded)
            currentPosition = motor.getCurrentPosition();
    }

    /** Calculates the wanted power to be sent to the motor */
    public void loopCore() {
        wantedPower = customLoop.invoke(this, target);
    }

    public void writeCore() {
        if (!ticker.shouldTick()) return;
        CoreOpMode.getInstance().setMotorPower(name, motor.getDcMotorEx(), wantedPower);
    }

    public T getStates() {
        return states;
    }

    private CoreState<Double> currState = null;

    @Override
    public CoreState<Double> currentState() {
        return currState;
    }
}
