package org.firstinspires.ftc.teamcode.MainOpModes.Teleops;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Components.Configs;

import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.utils.PreciseTimer;
import ro.sparktech24345.logicore.utils.TimeUnit;

@TeleOp(name = "Showcase TeleOP", group = "AAA")
public class ShowcaseTeleop extends CoreOpMode {
    public ShowcaseTeleop() {
        super(Configs.teleopCfg);
    }


    private final PreciseTimer mainTimer = new PreciseTimer();

    public void onInit() {
    }

    /**
     * TO DO
     * 1. rezolvat ca nu trimite la dashboard -- check
     * 2. Cu blaze pare ca avem niste probleme -- check
     * 3. OMA GAD KOTLIN E ASA ANNOYING        -- skill issue
     * 4. start la tickere pentru modelulele care se intampla separat decalat ex am 2 module care executa odata la 3 secunde, unul incepe la 0 celalalt la +2 -- check
     */
    public void onStart() {
        mainTimer.start();
    }

    public void onLoop() {
        coreTelemetry.addData("voltage", voltageSensor.voltage());
        coreTelemetry.addData("Loop time", mainTimer.time().get(TimeUnit.MILLIS));
//        coreTelemetry.addData("pos", getFollower().pose());
        mainTimer.start();
    }
}