package ro.sparktech24345.logicore.core;

import com.qualcomm.hardware.lynx.LynxModule;

import java.util.List;

import ro.sparktech24345.logicore.config.Hubs;

public class CoreHubs implements CoreModule {
    private List<LynxModule> hubs;

    public void initCore() {
        hubs = CoreOpMode.instance().hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
            if (hub.isParent()) Hubs.CONTROL.id(hub.getModuleAddress());
            else Hubs.EXPANSION.id(hub.getModuleAddress());
        }
    }

    public void loopCore() {
    }

    public void readCore() {
        for (LynxModule hub : hubs) hub.clearBulkCache();
    }
}