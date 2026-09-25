package ro.sparktech24345.logicore.core

import com.pedropathing.config.ConfigVar
import com.pedropathing.config.Configuration
import com.pedropathing.follower.Follower
import com.qualcomm.robotcore.hardware.HardwareMap
import ro.sparktech24345.logicore.pedro.FollowerConstants

class OpModeConfig {
    val type: ConfigVar<CoreOpMode.OpModeType> = ConfigVar.required()
    val followerConstants: ConfigVar<FollowerConstants> = ConfigVar.required()
    val performanceEngine: ConfigVar<CoreOpMode.PerformanceEngine> = ConfigVar.required()
    val useDriveTrain: ConfigVar<Boolean> = ConfigVar.of(true)
    val useFollower: ConfigVar<Boolean> = ConfigVar.of(true)

    val configSetup: ConfigVar<() -> Unit> = ConfigVar.of({})

    constructor(p0: Configuration<OpModeConfig>) {
        p0.configure(this)
    }
}