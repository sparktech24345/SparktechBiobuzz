package org.firstinspires.ftc.teamcode.Components;
import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;
public class RightTiltServoStateSet extends BaseStateSet<Double>{
    public RightTiltServoStateSet() {
        super();
    }

    public final CoreState<Double> ACTIVE = state(0.2, "R_ACTIVE");
    public final CoreState<Double> INACTIVE = state(0.85, "R_INACTIVE");
}
