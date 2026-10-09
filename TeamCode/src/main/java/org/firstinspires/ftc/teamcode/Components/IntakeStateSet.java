package org.firstinspires.ftc.teamcode.Components;

import android.util.Pair;
import ro.sparktech24345.logicore.states.StateSet;

public enum IntakeStateSet implements StateSet<Pair<IntakeMotorStateSet, CoupleServoStateSet>> {
    FULL_DECOUPLED("FULL_DECOUPLED", IntakeMotorStateSet.FULL, CoupleServoStateSet.DECOUPLED),
    HALF_DECOUPLED("HALF_DECOUPLED", IntakeMotorStateSet.HALF, CoupleServoStateSet.DECOUPLED),
    FULL_COUPLED  ("FULL_COUPLED",   IntakeMotorStateSet.FULL, CoupleServoStateSet.COUPLED  ),
    HALF_COUPLED  ("HALF_COUPLED",   IntakeMotorStateSet.HALF, CoupleServoStateSet.COUPLED  ),
    ZERO_COUPLED  ("ZERO_COUPLED",   IntakeMotorStateSet.ZERO, CoupleServoStateSet.COUPLED  ),
    ZERO_DECOUPLED("ZERO_DECOUPLED", IntakeMotorStateSet.ZERO, CoupleServoStateSet.DECOUPLED);
    private final String name;
    public static final IntakeStateSet DEFAULT = ZERO_COUPLED;
    private final Pair<IntakeMotorStateSet, CoupleServoStateSet> val;
    private IntakeStateSet(String name, IntakeMotorStateSet ms, CoupleServoStateSet ss) {
        this.val = new Pair<>(ms, ss);
        this.name = name;
    }

    @Override
    public String stateName() { return name; }

    @Override
    public Pair<IntakeMotorStateSet, CoupleServoStateSet> value() {
        return val;
    }
}
