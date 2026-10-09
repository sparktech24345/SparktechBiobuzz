package org.firstinspires.ftc.teamcode.Components;

import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;

public class LeftTiltServoStateSet extends BaseStateSet<Double> {
    public LeftTiltServoStateSet() {
        super();
    }

    public final CoreState<Double> ACTIVE = state(0.2, "L_ACTIVE");
    public final CoreState<Double> INACTIVE = state(.85, "L_INACTIVE");
}
