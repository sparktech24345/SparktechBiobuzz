package org.firstinspires.ftc.teamcode.Components;

import ro.sparktech24345.logicore.states.StateSet;

public enum IntakeMotorStateSet implements StateSet<Double> {
    FULL("FULL", 1.0),
    HALF("HALF", 0.5),
    HALF_REVERSED("HALF_REVERSED", -0.5),
    FULL_REVERSED("FULL_REVERSED", -1.0),
    ZERO("ZERO", .0);
    private IntakeMotorStateSet(String name, double value) {
        this.name = name;
        this.val = value;
    }

    private final String name;
    private final double val;
    @Override
    public Double value() {
        return val;
    }
    @Override
    public String stateName() {
        return name;
    }
}