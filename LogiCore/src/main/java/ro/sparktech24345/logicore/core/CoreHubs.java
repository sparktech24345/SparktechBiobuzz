package ro.sparktech24345.logicore.core;

import com.qualcomm.hardware.lynx.LynxModule;

import java.util.List;

public class CoreHubs implements CoreModule {
    private List<LynxModule> hubs;

    public void initCore() {
        hubs = CoreOpMode.getInstance().hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : hubs) hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
    }

    public void loopCore() {}
    public void readCore() {
        for (LynxModule hub : hubs) hub.clearBulkCache();
    }
}