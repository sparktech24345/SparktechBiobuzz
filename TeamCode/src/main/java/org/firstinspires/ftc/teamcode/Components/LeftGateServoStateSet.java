package org.firstinspires.ftc.teamcode.Components;

import ro.sparktech24345.logicore.states.StateSet;

public enum LeftGateServoStateSet implements StateSet<Double> {

    OPEN("L_OPEN", 0.65),
    CLOSED("L_CLOSED", 0.38);

    private final String name;
    private final double val;

    LeftGateServoStateSet(String name, double value) {
        this.name = name;
        this.val = value;
    }

    @Override
    public Double value() {
        return val;
    }

    public String stateName() {
        return name;
    }
}
