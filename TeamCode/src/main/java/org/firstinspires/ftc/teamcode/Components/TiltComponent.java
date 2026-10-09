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

public class TiltComponent implements CoreModule, HasStates<Pair<CoreState<Double>, CoreState<Double>>, TiltStateSet> {

    public final CoreServo<LeftTiltServoStateSet> leftTiltServo;
    public final CoreServo<RightTiltServoStateSet> rightTiltServo;
    public TiltComponent(){
        leftTiltServo= new CoreServo<>(GlobalStorage.leftTiltServoName,new LeftTiltServoStateSet());
        rightTiltServo = new CoreServo<>(GlobalStorage.rightTiltServoName,new RightTiltServoStateSet());
        this.states = new TiltStateSet(leftTiltServo.getStates(),rightTiltServo .getStates());
        states.own(this);
    }

    public CoreOpMode instance = null;

    public void initCore(){
        instance = CoreOpMode.getInstance();
        instance.install(Hubs.CONTROL,rightTiltServo,1.0);
        instance.install(Hubs.CONTROL,leftTiltServo,1.0);
        instance.execute(command(states.DEFAULT));
    }

    public void loopCore(){}
   private final TiltStateSet states;
    @Override
    public TiltStateSet getStates(){
        return states;
    }

    private CoreState<Pair<CoreState<Double>, CoreState<Double>>> currState = null;
    @Override
    public CoreState<Pair<CoreState<Double>, CoreState<Double>>> currentState() {
        return currState;
    }

    @Override
    public void setState(CoreState<Pair<CoreState<Double>, CoreState<Double>>> state) {
        CoreState<Double> fv = state.getValue().first;
        CoreState<Double> sv = state.getValue().second;
        fv.getOwner().setState(fv);
        sv.getOwner().setState(sv);
        currState = state;
    }
}
