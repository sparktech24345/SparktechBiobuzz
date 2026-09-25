package org.firstinspires.ftc.teamcode.MainOpModes.Teleops

import com.acmerobotics.dashboard.FtcDashboard
import com.pedropathing.drivetrain.DrivePowers
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.seattlesolvers.solverslib.hardware.motors.Motor
import org.firstinspires.ftc.teamcode.MyConstants
import org.firstinspires.ftc.teamcode.Pedro.ConstantsDecode
import ro.sparktech24345.logicore.config.ConfigMap
import ro.sparktech24345.logicore.config.Hubs
import ro.sparktech24345.logicore.config.MotorConfig
import ro.sparktech24345.logicore.core.CoreGamepad
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.core.OpModeConfig
import ro.sparktech24345.logicore.utils.PreciseTimer

@TeleOp(name = "Main TeleOP", group = "AAA")
class MainTeleOP : CoreOpMode(cfg) {

    companion object {
        val cfg = OpModeConfig { c ->
            c.type.set(OpModeType.TELEOP)
            c.performanceEngine.set(PerformanceEngine.BLAZE)
            c.useFollower.set(true)
            c.useDriveTrain.set(true)
            c.followerConstants.set(ConstantsDecode())
        }
    }

    var mainTimer = PreciseTimer()
    override fun onInit() {
        coreTelemetry.addTelemetry(FtcDashboard.getInstance().telemetry)
    }

    /**TO DO
     * 1. rezolvat ca nu trimite la deashboard -- check
     * 2. Cu blaze pare ca avem niste probleme -- check
     * 3. OMA GAD KOTLIN E ASA ANNOYING        -- skill issue
     * 4. start la tickere pentru modelulele care se intampla separat decalat ex am 2 module care executa odata la 3 secunde, unul incepe la 0 celalalt la +2 -- check
     */
    override fun onStart() {
        mainTimer.start()
    }

    override fun onLoop() {
        coreTelemetry.addData("voltage", voltageSensor.voltage)
        coreTelemetry.addData("Loop time", mainTimer.getTime().get(PreciseTimer.TimeUnit.MILLIS))
        coreTelemetry.addData("pos", coreFollower.pose.toString())
        mainTimer.start()
    }

    override fun registerConfig() {
        ConfigMap["frontleft"] = MotorConfig(Hubs.EXPANSION, -1)
        ConfigMap["backleft"] = MotorConfig(Hubs.EXPANSION, -1)
        ConfigMap["backright"] = MotorConfig(Hubs.EXPANSION, -1)
        ConfigMap["frontright"] = MotorConfig(Hubs.EXPANSION, -1)
    }
}