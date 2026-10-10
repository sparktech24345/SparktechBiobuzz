package org.firstinspires.ftc.teamcode.Components;

import android.util.Pair;

import ro.sparktech24345.logicore.states.StateSet;

public enum GateStateSet implements StateSet<Pair<LeftGateServoStateSet, RightGateServoStateSet>> {
    OPEN("GATES_OPEN", LeftGateServoStateSet.OPEN, RightGateServoStateSet.OPEN),
    CLOSED("GATES_CLOSED", LeftGateServoStateSet.CLOSED, RightGateServoStateSet.CLOSED),
    LEFT_OPEN("LEFT_OPEN", LeftGateServoStateSet.OPEN, RightGateServoStateSet.CLOSED),
    RIGHT_OPEN("RIGHT_OPEN", LeftGateServoStateSet.CLOSED, RightGateServoStateSet.OPEN);

    private final Pair<LeftGateServoStateSet, RightGateServoStateSet> val;
    private final String name;
    public static final GateStateSet DEFAULT = CLOSED;

    GateStateSet(String name, LeftGateServoStateSet lt, RightGateServoStateSet rt) {
        this.name = name;
        this.val = new Pair<>(lt, rt);
    }

    GateStateSet(String name, RightGateServoStateSet rt, LeftGateServoStateSet lt) {
        this(name, lt, rt);
    }

    @Override
    public Pair<LeftGateServoStateSet, RightGateServoStateSet> value() {
        return val;
    }

    @Override
    public String stateName() {
        return name;
    }
}
