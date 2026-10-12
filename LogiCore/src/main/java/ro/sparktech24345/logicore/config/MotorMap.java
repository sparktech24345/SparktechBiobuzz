package ro.sparktech24345.logicore.config;

import android.util.Pair;

import com.acmerobotics.dashboard.config.Config;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import dev.anygeneric.blazeftc.BlazeFTC;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.utils.Logger;

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
            if (o == null) continue;
            int id = Keys.keyId(key);
            if (id == Hubs.EXPANSION.id()) pe[Keys.keyPort(key)] = (double) o;
            else if (id == Hubs.CONTROL.id()) pc[Keys.keyPort(key)] = (double) o;
        }
        boolean writeC = false;
        for (double d : pc) {
            if (!Double.isNaN(d)) writeC = true;
        }
        boolean writeE = false;
        for (double d : pe) {
            if (!Double.isNaN(d)) writeE = true;
        }
        if (writeC && doBulkWrites) BlazeFTC.setMotorPowers(0, pc[0], pc[1], pc[2], pc[3]);
        else for (int i = 0; i < 4; ++i)
            if (Double.isFinite(pc[i]))
                BlazeFTC.setMotorPower(Hubs.CONTROL.id(), i, pc[i]);
        if (writeE && doBulkWrites) BlazeFTC.setMotorPowers(1, pe[0], pe[1], pe[2], pe[3]);
        else for (int i = 0; i < 4; ++i)
            if (Double.isFinite(pe[i]))
                BlazeFTC.setMotorPower(Hubs.EXPANSION.id(), i, pe[i]);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < 4; ++i) {
            out.append("CONTROL(").append(i).append(") = [").append(pc[i]).append("] -- EXPANSION(").append(i).append(") = [").append(pe[i]).append("]\n");
        }
        CoreOpMode.instance().logger().write(out.toString());
        powers.clear();
    }
}