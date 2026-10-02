package org.firstinspires.ftc.teamcode.Components;

import android.util.Pair;

import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;

public class IntakeMotorStateSet extends BaseStateSet<Double> {
    public IntakeMotorStateSet() {
        super();
        DEFAULT = ZERO;
    }

    public final CoreState<Double> FULL = state(1.0, "FULL");
    public final CoreState<Double> HALF = state(0.5, "HALF");
    public final CoreState<Double> HALF_REVERSED = state(-0.5, "HALF_REVERSED");
    public final CoreState<Double> FULL_REVERSED = state(-1.0, "FULL_REVERSED");
    public final CoreState<Double> ZERO = state(.0, "ZERO");
}