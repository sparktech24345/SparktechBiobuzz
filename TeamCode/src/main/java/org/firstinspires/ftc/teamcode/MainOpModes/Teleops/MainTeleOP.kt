package org.firstinspires.ftc.teamcode.MainOpModes.Teleops

import com.acmerobotics.dashboard.FtcDashboard
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.Pedro.ConstantsDecode
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.utils.PreciseTimer

@TeleOp(name = "Main Teleop", group = "AAA")
class MainTeleOP : CoreOpMode(OpModeType.TELEOP, ConstantsDecode(), PerformanceEngine.PHOTON) {
    var mainTimer = PreciseTimer()
    override fun onInit() {
        coreTelemetry.addTelemetry(FtcDashboard.getInstance().telemetry)
    }

    /**TO DO
     * 1. rezolvat ca nu trimite la deashboard
     * 2. Cu blaze pare ca avem niste probleme
     * 3. OMA GAD KOTLIN E ASA ANNOYING
     * 4. start la tickere pentru modelulele care se intampla separat decalat ex am 2 module care executa odata la 3 secunde, unul incepe la 0 celalalt la +2
     */
    override fun onStart() {
        mainTimer.start()
    }

    override fun onLoop() {
        coreTelemetry.addData("voltage", voltageSensor.voltage)
        coreTelemetry.addData("Loop time", mainTimer.getTime().get(PreciseTimer.TimeUnit.MILLIS))
        mainTimer.start()
    }
}