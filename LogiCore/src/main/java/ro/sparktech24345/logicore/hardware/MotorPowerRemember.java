package ro.sparktech24345.logicore.hardware;

import java.util.HashMap;
import java.util.Map;

public class MotorPowerRemember { // fully vibecoded caching for Blze

    private final Map<Integer, Double> powerCache = new HashMap<>(8);
    private final double epsilon;

    /**
     * @param epsilon Minimum power difference required to send a hardware command (e.g., 0.001)
     */
    public MotorPowerRemember(double epsilon) {
        this.epsilon = epsilon;
    }

    public MotorPowerRemember() {
        this(0.001);
    }

    /**
     * Generates a unique key for any motor across multiple hubs.
     * Combines hub ID (e.g., 173 or 2) and port (0-3).
     */
    public static int makeKey(int hubId, int port) {
        return (hubId << 8) | (port & 0xFF);
    }

    /**
     * Determines whether a new motor power command needs to be sent to hardware.
     * Returns true if no cached power exists or if the difference exceeds epsilon.
     */
    public boolean shouldWrite(int hubId, int port, double targetPower) {
        int key = makeKey(hubId, port);
        Double cachedPower = powerCache.get(key);

        if (cachedPower == null) {
            return true;
        }
        return Math.abs(targetPower - cachedPower) > epsilon;
    }

    /**
     * Updates the cached power value after issuing a command.
     */
    public void update(int hubId, int port, double targetPower) {
        int key = makeKey(hubId, port);
        powerCache.put(key, targetPower);
    }

    /**
     * Clears cached powers. Call this in your OpMode's init() phase.
     */
    public void reset() {
        powerCache.clear();
    }
}