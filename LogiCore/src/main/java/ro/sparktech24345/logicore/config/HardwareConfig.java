package ro.sparktech24345.logicore.config;

public class HardwareConfig {
    private int id;
    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }
    private int port;
    public int getPort() { return this.port; }
    public void setPort(int port) { this.port = port; }
    public HardwareConfig(int id, int port) {
        this.id = id;
        this.port = port;
    }

    public HardwareConfig(Hubs hub, int port) {
        this(hub.getId(), port);
    }
    public HardwareConfig(Hubs hub) { this(hub.getId()); }
    public HardwareConfig(int hubId) { this(hubId, -1); }
}