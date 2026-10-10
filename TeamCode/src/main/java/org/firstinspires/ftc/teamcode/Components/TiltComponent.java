package org.firstinspires.ftc.teamcode.Components;
import static ro.sparktech24345.logicore.commands.BaseCommand.command;

import android.util.Pair;

import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;

import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.hardware.CoreServo;
import ro.sparktech24345.logicore.states.StateSet;
import ro.sparktech24345.logicore.states.HasStates;

public class TiltComponent implements CoreModule, HasStates<Pair<LeftTiltServoStateSet, RightTiltServoStateSet>, TiltStateSet> {

    public final CoreServo<LeftTiltServoStateSet> leftTiltServo;
    public final CoreServo<RightTiltServoStateSet> rightTiltServo;
    public TiltComponent(){
        leftTiltServo= new CoreServo<>(GlobalStorage.leftTiltServoName,LeftTiltServoStateSet.INACTIVE);
        rightTiltServo = new CoreServo<>(GlobalStorage.rightTiltServoName,RightTiltServoStateSet.INACTIVE);
        this.states = TiltStateSet.class;
        state(TiltStateSet.DEFAULT);
    }

    public CoreOpMode instance = null;

    public void initCore(){
        instance = CoreOpMode.instance();
        instance.install(rightTiltServo,1.0);
        instance.install(leftTiltServo,1.0);
    }

    public void loopCore(){}
   private final Class<TiltStateSet> states;
    @Override
    public Class<TiltStateSet> states(){
        return states;
    }

    private TiltStateSet currState = null;
    @Override
    public TiltStateSet state() {
        return currState;
    }

    @Override
    public void state(TiltStateSet state) {
        leftTiltServo.state(state.value().first);
        rightTiltServo.state(state.value().second);
        currState = state;
    }
}
