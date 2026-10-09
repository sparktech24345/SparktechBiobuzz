package org.firstinspires.ftc.teamcode.Components;

import static ro.sparktech24345.logicore.commands.BaseCommand.command;

import android.util.Pair;

import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;

import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.hardware.CoreServo;
import ro.sparktech24345.logicore.states.HasStates;

public class GateComponent implements CoreModule, HasStates<Pair<LeftGateServoStateSet, RightGateServoStateSet>, GateStateSet> {
    public final CoreServo<LeftGateServoStateSet> leftGateServo;

    public final CoreServo<RightGateServoStateSet> rightGateServo;
    private CoreOpMode instance = null;

    public GateComponent() {
        leftGateServo = new CoreServo<>(GlobalStorage.leftGateServoName, LeftGateServoStateSet.CLOSED);
        rightGateServo = new CoreServo<>(GlobalStorage.rightGateServoName, RightGateServoStateSet.CLOSED);
        this.states = GateStateSet.class;
        state(GateStateSet.CLOSED);
    }


    /** Called once during OpMode initialization - set up hardware and initial state */
    public void initCore() {
        instance = CoreOpMode.instance();
        instance.install(leftGateServo, 1);
        instance.install(rightGateServo, 1);
    }

    /** Called every loop cycle - update module logic */
    public void loopCore() {}

    private final Class<GateStateSet> states;
    @Override
    public Class<GateStateSet> states() {
        return states;
    }

    private GateStateSet currState = null;
    @Override
    public GateStateSet state() {
        return currState;
    }

    @Override
    public void state(GateStateSet state) {
        leftGateServo.state(state.value().first);
        rightGateServo.state(state.value().second);
        currState = state;
    }
}