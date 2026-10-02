package org.firstinspires.ftc.teamcode.Components;

import android.util.Pair;

import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;

import ro.sparktech24345.logicore.commands.BaseCommand;
import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.hardware.CoreServo;
import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;
import ro.sparktech24345.logicore.states.HasStates;

public class GateComponent<T extends BaseStateSet<Pair<CoreState<Double>, CoreState<Double>>>> implements CoreModule, HasStates<Pair<CoreState<Double>, CoreState<Double>>, T> {
    public final CoreServo<LeftGateServoStateSet> leftGateServo =
            new CoreServo<>(GlobalStorage.leftGateServoName, new LeftGateServoStateSet());

    public final CoreServo<RightGateServoStateSet> rightGateServo =
            new CoreServo<>(GlobalStorage.rightGateServoName, new RightGateServoStateSet());
    public CoreOpMode instance = null;

    public GateComponent(T states) {
        this.states = states;
        states.own(this);
    }


    /** Called once during OpMode initialization - set up hardware and initial state */
    public void initCore() {
        instance = CoreOpMode.getInstance();
        instance.install(Hubs.CONTROL, leftGateServo, 1);
        instance.install(Hubs.CONTROL, rightGateServo, 1);
    }

    /** Called every loop cycle - update module logic */
    public void loopCore() {
//
    }

    private final T states;

    public static class LeftGateServoStateSet extends BaseStateSet<Double> {
        public LeftGateServoStateSet() { super(); }
        public final CoreState<Double> OPEN = state(.0, "L_OPEN");
        public final CoreState<Double> CLOSED = state(1.0, "L_CLOSED");
    }
    public static class RightGateServoStateSet extends BaseStateSet<Double> {
        public RightGateServoStateSet() { super(); }
        public final CoreState<Double> OPEN = state(.5, "R_OPEN");
        public final CoreState<Double> CLOSED = state(.67, "R_CLOSED");
    }

    @Override
    public T getStates() {
        return states;
    }

    @Override
    public <S extends CoreState<Pair<CoreState<Double>, CoreState<Double>>>> void setState(S state) {
        leftGateServo.setState(state.getValue().first);
        rightGateServo.setState(state.getValue().second);
    }
}