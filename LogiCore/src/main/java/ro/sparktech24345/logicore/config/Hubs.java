package ro.sparktech24345.logicore.config;

public enum Hubs {
    CONTROL(173),
    EXPANSION(2),
    INDEPENDENT(0);

    private final int id;
    public int getId() { return this.id; }

    Hubs(int id) {
        this.id = id;
    }
}