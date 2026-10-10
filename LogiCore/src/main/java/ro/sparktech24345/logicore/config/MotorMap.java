package ro.sparktech24345.logicore.config;

import android.util.Pair;

import com.acmerobotics.dashboard.config.Config;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import dev.anygeneric.blazeftc.BlazeFTC;

@Config
public class MotorMap {
    public static boolean doBulkWrites = true;
    private static final ConcurrentHashMap<Integer, Double> powers = new ConcurrentHashMap<>();

    public static void set(int key, double value) {
        powers.put(key, value);
    }

    public static double get(int key) {
        return Objects.requireNonNull(powers.get(key));
    }

    public static void write() {
        double[] pc = {Double.NaN, Double.NaN, Double.NaN, Double.NaN};
        double[] pe = {Double.NaN, Double.NaN, Double.NaN, Double.NaN};
        for (int key : powers.keySet()) {
            Object o = powers.get(key);
            if (o != null) switch (Keys.keyId(key)) {
                case 2:
                    pe[Keys.keyPort(key)] = (double) o;
                    break;
                case 173:
                    pc[Keys.keyPort(key)] = (double) o;
                    break;
                default:
                    break;
            }
        }
        boolean writeC = true;
        for (double d : pc) {
            if (!Double.isFinite(d)) writeC = false;
            break;
        }
        boolean writeE = true;
        for (double d : pe) {
            if (!Double.isFinite(d)) writeE = false;
            break;
        }
        if (writeC && doBulkWrites) BlazeFTC.setMotorPowers(0, pc[0], pc[1], pc[2], pc[3]);
        else for (int i = 0; i < 4; ++i)
            if (Double.isFinite(pc[i]))
                BlazeFTC.setMotorPower(Hubs.CONTROL.id(), i, pc[i]);
        if (writeE && doBulkWrites) BlazeFTC.setMotorPowers(1, pe[0], pe[1], pe[2], pe[3]);
        else for (int i = 0; i < 4; ++i)
            if (Double.isFinite(pe[i]))
                BlazeFTC.setMotorPower(Hubs.EXPANSION.id(), i, pe[i]);
        powers.clear();
    }
}