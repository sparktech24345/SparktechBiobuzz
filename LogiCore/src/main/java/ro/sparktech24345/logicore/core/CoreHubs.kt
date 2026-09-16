package ro.sparktech24345.logicore.core

import com.qualcomm.hardware.lynx.LynxModule

class CoreHubs: CoreModule {
    val hubs: MutableList<LynxModule> = mutableListOf()

    override fun init() {
        hubs.addAll(CoreOpMode.instance!!.hardwareMap.getAll(LynxModule::class.java))
        for (hub in hubs) hub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
    }

    override fun start() = loop()
    override fun init_loop() = loop()
    override fun stop() = loop()
    override fun loop() {
        for (hub in hubs) hub.clearBulkCache()
    }

}