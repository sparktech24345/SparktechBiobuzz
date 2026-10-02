package org.firstinspires.ftc.teamcode.Components;

import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;

public class RightGateServoStateSet extends BaseStateSet<Double> {
    public RightGateServoStateSet() {
        super();
    }

    public final CoreState<Double> OPEN = state(.5, "R_OPEN");
    public final CoreState<Double> CLOSED = state(.67, "R_CLOSED");
}
