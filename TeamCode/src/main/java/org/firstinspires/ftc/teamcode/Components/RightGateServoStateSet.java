package org.firstinspires.ftc.teamcode.Components;


import ro.sparktech24345.logicore.states.StateSet;

public enum RightGateServoStateSet implements StateSet<Double> {
    OPEN("R_OPEN", 0.42),
    CLOSED("R_CLOSED", 0.65);

    private final String name;
    private final double val;

    RightGateServoStateSet(String name, double value) {
        this.name = name;
        this.val = value;
    }

    @Override
    public Double value() {
        return this.val;
    }

    public String stateName() {
        return this.name;
    }
}
