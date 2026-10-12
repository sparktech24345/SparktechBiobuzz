package ro.sparktech24345.logicore.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import kotlin.jvm.functions.Function2;
import ro.sparktech24345.logicore.config.IsHardware;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.states.HasStates;
import ro.sparktech24345.logicore.states.StateSet;
import ro.sparktech24345.logicore.utils.CachingTracker;
import ro.sparktech24345.logicore.utils.TickInterval;

/**
 * Enhanced motor control with state management, PID control, and multiple run modes.
 * Supports power, position, velocity, and custom control modes with encoder integration.
 */
public class CoreMotor<T extends StateSet<Double>> implements CoreModule, HasStates<Double, T>, IsHardware {
    public CoreMotor(String name, T initialState) {
        this(name, initialState, 0.02);
    }

    public CoreMotor(String name, T initialState, double threshold) {
        this(name, initialState, threshold, 1);
    }

    public CoreMotor(String name, T initialState, double threshold, double interval) {
        this.name = name;
        this.states = (Class<T>) initialState.getClass();
        state(initialState);
        this.threshold = threshold;
        this.ticker = new TickInterval(interval);
    }

    private final String name;
    private int key = 0;
    @Override
    public void key(int key) {
        this.key = key;
    }

    @Override
    public int key() {
        return this.key;
    }

    private CachingMotor motor;

    /** The motor, wrapped in a CachingMotor (only changed powers are sent). */
    public CachingMotor motor() {
        return this.motor;
    }

    private final TickInterval ticker;

    public TickInterval ticker() {
        return this.ticker;
    }
    /** Minimum power change that gets sent to the hub (passed to the CachingMotor). */
    private double threshold;

    public double threshold() {
        return this.threshold;
    }

    public void threshold(double value) {
        this.threshold = value;
        if (motor != null) motor.threshold(value);
    }

    public CachingTracker tracker() {
        return motor == null ? null : motor.tracker();
    }

    private final Class<T> states;
    private T currentState;

    /**
     * Set the motor to a specific state.
     * Automatically handles run mode switching based on state type.
     */
    @Override
    public Class<T> states() {
        return states;
    }

    @Override
    public T state() {
        return currentState;
    }

    @Override
    public void state(T state) {
        this.currentState = state;
        this.target = currentState.value();
    }

    private double target = 0;

    private double wantedPower = 0;

    /**
     * Whether to update this motor during init_loop stage
     */
    private boolean updateInInit = false;

    public void doUpdatesInInit(boolean value) {
        this.updateInInit = value;
    }

    public boolean doesUpdatesInInit() {
        return this.updateInInit;
    }

    /**
     * Whether this motor has an encoder installed
     */
    private boolean encoded = false;

    public void encoded(boolean val) {
        if (encoded != val) encoderChange = true;
        this.encoded = val;
    }

    public boolean encoded() {
        return this.encoded;
    }

    private boolean encoderChange = false;

    /**
     * Encoder ticks per revolution (auto-detected if possible)
     */
    private double unitsPerRev = 1;

    public void unitsPerRev(double value) {
        this.unitsPerRev = value;
    }

    public double unitsPerRev() {
        return this.unitsPerRev;
    }

    /**
     * Current encoder position in ticks
     */
    private double currentPosition = Double.NaN;

    public double currentPosition() {
        return this.currentPosition;
    }

    /**
     * Motor behavior when power is set to 0
     */
    private DcMotor.ZeroPowerBehavior zeroPowerBehavior = DcMotor.ZeroPowerBehavior.UNKNOWN;

    public void zeroPowerBehavior(DcMotor.ZeroPowerBehavior zpb) {
        this.zeroPowerBehavior = zpb;
    }

    public DcMotor.ZeroPowerBehavior zeroPowerBehavior() {
        return this.zeroPowerBehavior;
    }


    private DcMotorSimple.Direction direction = DcMotorSimple.Direction.FORWARD;

    public void direction(DcMotorSimple.Direction dir) {
        this.direction = dir;
    }

    public DcMotorSimple.Direction direction() {
        return this.direction;
    }

    private final Function2<CoreMotor, Double, Double> DEFAULT_LOOP = (m, t) -> t;

    /**
     * Custom control loop function for advanced motor control
     */
    private Function2<CoreMotor, Double, Double> customLoop = DEFAULT_LOOP;

    public void loop(Function2<CoreMotor, Double, Double> fn) {
        this.customLoop = fn;
    }

    public void loop() {
        this.customLoop = DEFAULT_LOOP;
    }

    /**
     * Reverse motor direction
     */
    public void reverse() {
        reverse(true);
    }

    public void reverse(boolean enabled) {
        direction(enabled ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void initCore() {
        motor = new CachingMotor(CoreOpMode.instance().hardwareMap.get(DcMotorImplEx.class, name), threshold);
        key = motor.key();
        unitsPerRev = motor.getController().getMotorType(motor.getPortNumber()).getTicksPerRev();
    }

    @Override
    public void init_loopCore() {
        if (updateInInit) loopCore();
    }

    @Override
    public void readCore() {
        // CachingMotor only talks to the hub when these actually change, so applying them every loop is free
        if (zeroPowerBehavior != DcMotor.ZeroPowerBehavior.UNKNOWN)
            motor.setZeroPowerBehavior(zeroPowerBehavior);
        motor.setDirection(direction);
        if (encoderChange) // setMode always talks to the hub, so it stays change-gated
            motor.setMode(encoded ? DcMotor.RunMode.RUN_USING_ENCODER : DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        encoderChange = false;
        if (encoded)
            currentPosition = motor.getCurrentPosition() / unitsPerRev;
    }

    /**
     * Calculates the wanted power to be sent to the motor
     */
    @Override
    public void loopCore() {
        wantedPower = customLoop.invoke(this, target);
    }

    public void motorPower(double pow) {
        this.wantedPower = pow;
    }

    public double motorPower() {
        return wantedPower;
    }

    @Override
    public void writeCore() {
        if (!ticker.shouldTick()) return;
        motor.setPower(wantedPower);
    }

    public double velocity() {
        if (motor == null) return 0;
        return motor.getVelocity();
    }
}
