package org.firstinspires.ftc.teamcode.Components;

import com.acmerobotics.dashboard.config.Config;

import ro.sparktech24345.logicore.states.StateSet;

@Config
public enum CoupleServoStateSet implements StateSet<Double> {
    COUPLED("COUPLED", 0.14),
    DECOUPLED("DECOUPLED", 0.25);

    private final String name;

    @Override
    public String stateName() {
        return name;
    }


    private final double value;

    @Override
    public Double value() {
        return value;
    }

    CoupleServoStateSet(String name, double value) {
        this.name = name;
        this.value = value;
    }
}
