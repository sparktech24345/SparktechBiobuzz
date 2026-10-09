package org.firstinspires.ftc.teamcode.Components;
import android.util.Pair;

import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;

public class TiltStateSet extends BaseStateSet<Pair<CoreState<Double>, CoreState<Double>>> {
    public TiltStateSet(LeftTiltServoStateSet lt, RightTiltServoStateSet rt) {
        super();
        ACTIVE       = state(new Pair<>(lt.ACTIVE, rt.ACTIVE), "ACTIVE");
        INACTIVE     = state(new Pair<>(lt.INACTIVE, rt.INACTIVE), "INACTIVE");
        DEFAULT = INACTIVE;

    }
    public TiltStateSet(RightTiltServoStateSet rt, LeftTiltServoStateSet lt) {
        this(lt, rt);
    }
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> ACTIVE;
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> INACTIVE;
}
