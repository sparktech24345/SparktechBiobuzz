package org.firstinspires.ftc.teamcode.Helpers;

import static org.firstinspires.ftc.teamcode.Helpers.GlobalStorage.camId;

public enum GetColorCase {
    GPP,
    PGP,
    PPG,
    NOSORT;

    public static GetColorCase getCase() {
        if (camId == 1) return GPP;
        if (camId == 2) return PGP;
        if (camId == 3) return PPG;
        else return NOSORT;
    }

}
