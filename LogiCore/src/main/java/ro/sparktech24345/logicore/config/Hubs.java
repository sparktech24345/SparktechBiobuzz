package ro.sparktech24345.logicore.config;

public enum Hubs {
    CONTROL(173),
    EXPANSION(2),
    INDEPENDENT(0);

    private volatile short id;

    public short id() {
        return this.id;
    }

    /** Overridden by CoreHubs at init with the real module addresses. */
    public void id(int id) {
        this.id = (short) id;
    }

    Hubs(short id) {
        this.id = id;
    }

    Hubs(int id) {
        this(id > Short.MAX_VALUE || id < Short.MIN_VALUE ? (short) 0 : (short) id);
    }
}