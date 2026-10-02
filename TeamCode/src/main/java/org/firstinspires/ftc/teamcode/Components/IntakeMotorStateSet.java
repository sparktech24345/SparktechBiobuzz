package org.firstinspires.ftc.teamcode.Components;

import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;

public class IntakeMotorStateSet extends BaseStateSet {
    public IntakeMotorStateSet() {
        super();
    }

    public final CoreState FULL = register(new CoreState(1, "FULL"));
    // register() face ca state-ul sa fie detinut de motorul care foloseste StateSet-ul
    public final CoreState HALF = register(new CoreState(.5, "HALF"));
    // adica state-ul poate sa tina minte ownerul state-ului
    public final CoreState HALF_REVERSED = register(new CoreState(-0.5, "HALF_REVERSED"));
    public final CoreState FULL_REVERSED = register(new CoreState(-1, "FULL_REVERSED"));
}
