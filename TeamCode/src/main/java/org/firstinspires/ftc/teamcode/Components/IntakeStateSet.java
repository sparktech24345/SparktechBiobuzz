package org.firstinspires.ftc.teamcode.Components;

import android.util.Pair;

import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;
import ro.sparktech24345.logicore.states.HasStates;

public class IntakeStateSet extends BaseStateSet<Pair<CoreState<Double>, CoreState<Double>>> {
    public final IntakeMotorStateSet motorStates;
    public final CoupleServoStateSet servoStates;
    public IntakeStateSet(IntakeMotorStateSet mStates, CoupleServoStateSet sStates) {
        super();
        this.motorStates = mStates;
        this.servoStates = sStates;
        HALF_REVERSED = state(new Pair<>(motorStates.FULL, servoStates.DECOUPLED), "HALF_REVERSED");
        HALF          = state(new Pair<>(motorStates.FULL_REVERSED, servoStates.DECOUPLED), "HALF");
        FULL          = state(new Pair<>(motorStates.FULL, servoStates.COUPLED), "FULL");
        FULL_REVERSED = state(new Pair<>(motorStates.FULL_REVERSED, servoStates.COUPLED), "FULL_REVERSED");
    }

    // register() face ca state-ul sa fie detinut de motorul care foloseste StateSet-ul
    // adica state-ul poate sa tina minte ownerul state-ului
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> HALF_REVERSED;
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> HALF         ;
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> FULL         ;
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> FULL_REVERSED;
}
