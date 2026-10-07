package org.firstinspires.ftc.teamcode.Components;

import com.acmerobotics.dashboard.config.Config;

import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;

@Config
public class CoupleServoStateSet extends BaseStateSet<Double> {
    public CoupleServoStateSet() {
        super();
    }
    public static double coupled_servo = 0.14;
    public static double decoupled_servo = 0.25;

    public final CoreState<Double> COUPLED = state(coupled_servo * 1, "COUPLED");
    public final CoreState<Double> DECOUPLED = state(decoupled_servo * 1, "DECOUPLED");
}
