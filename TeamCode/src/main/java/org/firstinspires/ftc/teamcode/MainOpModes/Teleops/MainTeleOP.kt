package org.firstinspires.ftc.teamcode.MainOpModes.Teleops;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Pedro.ConstantsDecode;
import org.jetbrains.annotations.NotNull;

import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.pedro.FollowerConstants;
import ro.sparktech24345.logicore.utils.DriveTrain;

@TeleOp(name = "Main Teleop", group = "AAA")
public class MainTeleop extends CoreOpMode {
    ElapsedTime mainTimer = new ElapsedTime();
    public MainTeleop() {
        super(OpModeType.TELEOP, new ConstantsDecode(), PerformanceEngine.PHOTON);
    }

    @Override
    public void onInit() {

    }

    /**TO DO
     * 1. rezolvat ca nu trimite la deashboard
     * 2. Cu blaze pare ca avem niste probleme
     * 3. OMA GAD KOTLIN E ASA ANNOYING
     * 4. start la tickere pentru modelulele care se intampla separat decalat ex am 2 module care executa odata la 3 secunde, unul incepe la 0 celalalt la +2
     * */





    @Override
    public void onStart() {
        super.onStart();
        mainTimer.reset();
    }

    @Override
    public void onLoop() {
        double time = mainTimer.milliseconds();
        mainTimer.reset();
        telemetry.addData("Loop time",time);
        telemetry.addData("voltage",getVoltageSensor().getVoltage());
    }
}
