package org.firstinspires.ftc.teamcode.Components;

import static ro.sparktech24345.logicore.commands.BaseCommand.command;

import android.util.Pair;

import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;

import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.hardware.CoreServo;
import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;
import ro.sparktech24345.logicore.states.HasStates;

public class GateComponent implements CoreModule, HasStates<Pair<CoreState<Double>, CoreState<Double>>, GateStateSet> {
    public final CoreServo<LeftGateServoStateSet> leftGateServo;

    public final CoreServo<RightGateServoStateSet> rightGateServo;
    private CoreOpMode instance = null;

    public GateComponent() {
        leftGateServo = new CoreServo<>(GlobalStorage.leftGateServoName, new LeftGateServoStateSet());
        rightGateServo = new CoreServo<>(GlobalStorage.rightGateServoName, new RightGateServoStateSet());
        this.states = new GateStateSet(leftGateServo.getStates(), rightGateServo.getStates());
        states.own(this);
    }


    /** Called once during OpMode initialization - set up hardware and initial state */
    public void initCore() {
        instance = CoreOpMode.getInstance();
        instance.install(Hubs.CONTROL, leftGateServo, 1);
        instance.install(Hubs.CONTROL, rightGateServo, 1);
        instance.execute(command(states.DEFAULT)); // nu e necesar dar recomand sa puneti asta
    }

    /** Called every loop cycle - update module logic */
    public void loopCore() {}

    private final GateStateSet states;
    @Override
    public GateStateSet getStates() {
        return states;
    }

    @Override
    public <S extends CoreState<Pair<CoreState<Double>, CoreState<Double>>>> void setState(S state) {
        CoreState<Double> fv = state.getValue().first;
        CoreState<Double> sv = state.getValue().second;
        fv.getOwner().setState(fv);
        sv.getOwner().setState(sv);
    }
}