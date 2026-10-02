package org.firstinspires.ftc.teamcode.Components;

import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;

public class CoupleServoStateSet extends BaseStateSet<Double> {
    public CoupleServoStateSet() {
        super();
    }

    public final CoreState<Double> COUPLED = state(0.14 * 360, "COUPLED");
    public final CoreState<Double> DECOUPLED = state(0.25 * 360, "DECOUPLED");
}
