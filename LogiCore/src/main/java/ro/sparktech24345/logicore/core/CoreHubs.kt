package ro.sparktech24345.logicore.core

import com.qualcomm.hardware.lynx.LynxModule

class CoreHubs: CoreModule {
    private lateinit var hubs: MutableList<LynxModule>

    override fun initCore() {
        hubs = CoreOpMode.instance!!.hardwareMap.getAll(LynxModule::class.java).toMutableList()
        for (hub in hubs) hub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
    }

    override fun loopCore() = Unit
    override fun readCore() {
        for (hub in hubs) {
            CoreOpMode.instance!!.coreTelemetry.addLine("hub $hub")
            CoreOpMode.instance!!.coreTelemetry.addLine("hub id stuff" + hub.moduleAddress)
            hub.clearBulkCache()
        }
    }

}