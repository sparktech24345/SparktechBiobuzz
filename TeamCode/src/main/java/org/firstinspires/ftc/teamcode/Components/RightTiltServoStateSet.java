package org.firstinspires.ftc.teamcode.Components;
import ro.sparktech24345.logicore.states.StateSet;
public enum RightTiltServoStateSet implements StateSet<Double>{

    ACTIVE("R_ACTIVE", 0.2),
    INACTIVE("R_INACTIVE", 0.85);

    private final String name;
    private final double val;

    RightTiltServoStateSet(String name, double value) {
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
