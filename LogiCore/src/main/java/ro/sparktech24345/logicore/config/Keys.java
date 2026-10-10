package ro.sparktech24345.logicore.config;

import kotlin.text.Regex;

public class Keys {

    private static final int SHORT_MASK = 0xFFFF;

    public static int key(int id, int port) {
        return ((id & SHORT_MASK) << 16) | (port & SHORT_MASK);
    }

    public static int key(String conn, int port) {
        Regex rg = new Regex("(?<=module )[0-9]*");
        int id = Integer.parseInt(String.valueOf(rg.find(conn, 0)));
        return key(id, port);
    }

    public static int keyId(int key) {
        return (key >> 16) & SHORT_MASK;
    }

    public static int keyPort(int key) {
        return (key & SHORT_MASK);
    }
}