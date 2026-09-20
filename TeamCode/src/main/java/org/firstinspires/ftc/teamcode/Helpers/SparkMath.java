package org.firstinspires.ftc.teamcode.Helpers;

import android.util.Pair;

public class SparkMath {
    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double angle_clamp(double val, double min, double max) {
        while (val < min) val += Math.abs(min);
        while (val > max) val -= Math.abs(max);
        return val;
    }

    public static double eval(boolean val) {
        return (val ? 1 : 0);
    }
    public static boolean eval(double val) {
        return val != 0;
    }
    public static boolean evalForTrigger(double val) {
        return val >= 0.4;
    }
    public static boolean TriggerEval(double val) {
        return val > 0.4;
    }
    public static <Tx, Ty> Pair<Tx, Ty> make_pair(Tx arg1, Ty arg2) { return new Pair<>(arg1, arg2); }
    public static double interpolate(double x, double x1, double x2, double y1, double y2) {
        double tangent = (y2 - y1) / (x2 - x1);
        return (y1 + tangent * (x - x1));
    }
}
