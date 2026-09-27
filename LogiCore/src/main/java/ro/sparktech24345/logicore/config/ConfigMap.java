package ro.sparktech24345.logicore.config;

import java.util.concurrent.ConcurrentHashMap;

public class ConfigMap {
    private static final ConcurrentHashMap<String, HardwareConfig> map = new ConcurrentHashMap<>();

    public static void set(String name, HardwareConfig value) {
        map.put(name, value);
    }

    public static HardwareConfig get(String name) {
        return map.get(name);
    }
}