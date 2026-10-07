package org.firstinspires.ftc.teamcode.Components;

import android.util.Pair;

import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;
import ro.sparktech24345.logicore.states.HasStates;

public class IntakeStateSet extends BaseStateSet<Pair<CoreState<Double>, CoreState<Double>>> {
    public IntakeStateSet(IntakeMotorStateSet ms, CoupleServoStateSet ss) {
        super(); // ms inseamna motor states si ss inseamna servo states (pt intake / coupling)
        FULL_DECOUPLED = state(new Pair<>(ms.FULL, ss.DECOUPLED), "FULL_DECOUPLED");
        HALF_DECOUPLED = state(new Pair<>(ms.HALF, ss.DECOUPLED), "HALF_DECOUPLED");
        FULL_COUPLED   = state(new Pair<>(ms.FULL, ss.COUPLED  ), "FULL_COUPLED");
        HALF_COUPLED   = state(new Pair<>(ms.HALF, ss.COUPLED  ), "HALF_COUPLED");
        ZERO_COUPLED   = state(new Pair<>(ms.ZERO, ss.COUPLED  ), "ZERO_COUPLED");
        ZERO_DECOUPLED = state(new Pair<>(ms.ZERO, ss.DECOUPLED), "ZERO_DECOUPLED");

        // ^ aici astea le faci tu cum vrei
        DEFAULT = ZERO_DECOUPLED;
    }

    public IntakeStateSet(CoupleServoStateSet ss, IntakeMotorStateSet ms) { this(ms, ss); }
    // la fel, ^ facut ca sa poti sa le pui si invers

    // si aici le declari
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> FULL_DECOUPLED;
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> HALF_DECOUPLED;
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> FULL_COUPLED;
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> HALF_COUPLED;
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> ZERO_COUPLED;
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> ZERO_DECOUPLED;

    // also `CoreState<Pair<CoreState<Double>, CoreState<Double>>>` pare complicat, dar e practic un
    // state care tine o pereche de state-uri care fiecare tine un Double
    // si pt. motor + servo ai state-ul motorului + state-ul servoului = state-ul componentei
}
