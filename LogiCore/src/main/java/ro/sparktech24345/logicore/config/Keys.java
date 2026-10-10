package ro.sparktech24345.logicore.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Keys {

    private static final int SHORT_MASK = 0xFFFF;

    public static int key(int id, int port) {
        return ((id & SHORT_MASK) << 16) | (port & SHORT_MASK);
    }

    public static int key(String conn, int port) {
        Pattern pattern = Pattern.compile("(?<=module )[0-9]*");
        Matcher matcher = pattern.matcher(conn);
        int id = 0;
        if (matcher.find()) id = Integer.parseInt(matcher.group());
        return key(id, port);
    }

    public static int keyId(int key) {
        return (key >> 16) & SHORT_MASK;
    }

    public static int keyPort(int key) {
        return (key & SHORT_MASK);
    }
}