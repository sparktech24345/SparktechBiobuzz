package org.firstinspires.ftc.teamcode.Components;

import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;

public class LeftGateServoStateSet extends BaseStateSet<Double> {
    public LeftGateServoStateSet() {
        super();
    }

    public final CoreState<Double> OPEN = state(0.65, "L_OPEN");
    public final CoreState<Double> CLOSED = state(0.38, "L_CLOSED");
}
