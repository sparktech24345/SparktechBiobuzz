package org.firstinspires.ftc.teamcode.Components

import org.firstinspires.ftc.teamcode.MainOpModes.Teleops.TestTeleOP
import org.firstinspires.ftc.teamcode.Pedro.ConstantsDecode
import ro.sparktech24345.logicore.config.ConfigMap
import ro.sparktech24345.logicore.config.HardwareConfig
import ro.sparktech24345.logicore.config.Hubs
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.core.OpModeConfig
import ro.sparktech24345.logicore.hardware.CoreMotor

class Configs {
    companion object {
        val teleopCfg = OpModeConfig { opModeConfig ->
            opModeConfig.type.set(CoreOpMode.OpModeType.TELEOP)
            opModeConfig.performanceEngine.set(CoreOpMode.PerformanceEngine.BLAZE)
            opModeConfig.useFollower.set(true)
            opModeConfig.useDriveTrain.set(true)
            opModeConfig.followerConstants.set(ConstantsDecode())
            opModeConfig.configSetup.set {
                ConfigMap["frontleft"] = HardwareConfig(Hubs.EXPANSION, -1)
                ConfigMap["backleft"] = HardwareConfig(Hubs.EXPANSION, -1)
                ConfigMap["backright"] = HardwareConfig(Hubs.EXPANSION, -1)
                ConfigMap["frontright"] = HardwareConfig(Hubs.EXPANSION, -1)

                ConfigMap["rightintakemotor"] = HardwareConfig(Hubs.CONTROL, -1)
                ConfigMap["leftintakemotor"] = HardwareConfig(Hubs.CONTROL, -1)

                ConfigMap["rightouttakemotor"] = HardwareConfig(Hubs.CONTROL, -1)
                ConfigMap["leftouttakemotor"] = HardwareConfig(Hubs.CONTROL, -1)
            }

        }

        // motors

        val exampleMotor = CoreMotor("motorleft", TestTeleOP.MotorTestStateSet())


        // components


        //install components

        fun installBot() {
            val instance = CoreOpMode.instance!!
            instance.install(Hubs.CONTROL, exampleMotor, 1.0F) // cInstall - instaleaza module specifice control hub-ului
        }
    }
}