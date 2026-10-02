package org.firstinspires.ftc.teamcode.Components;

import android.util.Pair;

import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;

public class GateStateSet extends BaseStateSet<Pair<CoreState<Double>, CoreState<Double>>> {
    public GateStateSet(LeftGateServoStateSet lt, RightGateServoStateSet rt) {
        super();
        OPEN       = state(new Pair<>(lt.OPEN  , rt.OPEN  ), "GATES_OPEN"  );
        CLOSED     = state(new Pair<>(lt.CLOSED, rt.CLOSED), "GATES_CLOSED");
        LEFT_OPEN  = state(new Pair<>(lt.OPEN  , rt.CLOSED), "LEFT_OPEN"   );
        RIGHT_OPEN = state(new Pair<>(lt.CLOSED, rt.OPEN  ), "RIGHT_OPEN"  );
        DEFAULT = CLOSED;
    }
    public GateStateSet(RightGateServoStateSet rt, LeftGateServoStateSet lt) {
        this(lt, rt);
    } // facut sa poti sa pui si invers dreapta / stanga in caz ca gresesti

    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> OPEN;
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> CLOSED;
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> LEFT_OPEN;
    public final CoreState<Pair<CoreState<Double>, CoreState<Double>>> RIGHT_OPEN;
}
