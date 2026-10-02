package org.firstinspires.ftc.teamcode.Components;

import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;

import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.hardware.CoreServo;
import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;
import ro.sparktech24345.logicore.states.HasStates;

public class GateComponent<T extends BaseStateSet> implements CoreModule, HasStates<T> {

    public final CoreServo<BaseStateSet> leftGateServo =
            new CoreServo<>(GlobalStorage.leftGateServoName, new BaseStateSet());

    public final CoreServo<BaseStateSet> rightGateServo =
            new CoreServo<>(GlobalStorage.rightGateServoName, new BaseStateSet());
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

    private T states;

    public static class IntakeMotorStateSet extends BaseStateSet {
        public IntakeMotorStateSet() { super(); }
        public final CoreState COUPLED = register(new CoreState(0.14 * 360, "COUPLED"));
        public final CoreState DECOUPLED = register(new CoreState( 0.25 * 360, "DECOUPLED"));
    }
    @Override
    public T getStates() {
        return states;
    }

    @Override
    public <S extends CoreState> void setState(S state) {
        leftGateServo.setState(state);
    }
}