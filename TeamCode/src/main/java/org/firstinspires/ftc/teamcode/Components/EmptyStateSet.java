package org.firstinspires.ftc.teamcode.Components;

import com.acmerobotics.dashboard.config.Config;

import ro.sparktech24345.logicore.states.StateSet;

@Config
public enum EmptyStateSet implements StateSet<Double> {
    ZERO("ZERO", 0.0);

    private final String name;
    @Override
    public String stateName() { return name; }


    private final double value;
    @Override
    public Double value() { return value; }
    EmptyStateSet(String name, double value) {
        this.name = name;
        this.value = value;
    }
}
