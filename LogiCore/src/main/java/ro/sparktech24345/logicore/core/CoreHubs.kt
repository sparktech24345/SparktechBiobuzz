package ro.sparktech24345.logicore.core

import com.qualcomm.hardware.lynx.LynxModule

class CoreHubs: CoreModule {
    val hubs: MutableList<LynxModule> = mutableListOf()

    override fun initCore() {
        hubs.addAll(CoreOpMode.instance!!.hardwareMap.getAll(LynxModule::class.java))
        for (hub in hubs) hub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
    }

    override fun startCore() = loopCore()
    override fun init_loopCore() = loopCore()
    override fun stopCore() = loopCore()
    override fun loopCore() {
        for (hub in hubs) hub.clearBulkCache()
    }

}