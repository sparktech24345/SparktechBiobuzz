package org.firstinspires.ftc.teamcode.Components;
import android.util.Pair;

import ro.sparktech24345.logicore.states.StateSet;

public enum TiltStateSet implements StateSet<Pair<LeftTiltServoStateSet, RightTiltServoStateSet>> {
    ACTIVE("PARK", LeftTiltServoStateSet.ACTIVE, RightTiltServoStateSet.ACTIVE),
    INACTIVE("NO_PARK", LeftTiltServoStateSet.INACTIVE, RightTiltServoStateSet.INACTIVE);

    private final Pair<LeftTiltServoStateSet, RightTiltServoStateSet> val;
    private final String name;
    public static final TiltStateSet DEFAULT = INACTIVE;

    TiltStateSet(String name, LeftTiltServoStateSet lt, RightTiltServoStateSet rt) {
        this.name = name;
        this.val = new Pair<>(lt, rt);
    }

    TiltStateSet(String name, RightTiltServoStateSet rt, LeftTiltServoStateSet lt) {
        this(name, lt, rt);
    }

    @Override
    public Pair<LeftTiltServoStateSet, RightTiltServoStateSet> value() {
        return val;
    }

    @Override
    public String stateName() {
        return name;
    }
}
