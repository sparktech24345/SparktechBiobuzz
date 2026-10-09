package org.firstinspires.ftc.teamcode.MainOpModes.Teleops;

import static org.firstinspires.ftc.teamcode.Components.Configs.installBot;
import static ro.sparktech24345.logicore.commands.BaseCommand.command;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Components.Configs;
import org.firstinspires.ftc.teamcode.Components.EmptyStateSet;
import org.firstinspires.ftc.teamcode.Components.TurretComponent;

import ro.sparktech24345.logicore.core.Button;
import ro.sparktech24345.logicore.core.CoreButton;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.events.EventBus;
import ro.sparktech24345.logicore.hardware.CoreServo;
import ro.sparktech24345.logicore.utils.PreciseTimer;

@TeleOp(name = "Test Op Mode", group = "Testing")
public class TestTeleOP extends CoreOpMode {
    public TestTeleOP() {
        super(Configs.teleopCfg);
    }

    public final PreciseTimer timer = new PreciseTimer();
    public final CoreServo<EmptyStateSet> servo =
            new CoreServo<>("sample_servo", EmptyStateSet.ZERO);
    public final TurretComponent turret = new TurretComponent();

    public void onInit() {
        timer.start(); // doar reseteaza timerul
        install(servo, 1);
        install(turret, 1);

        installBot(); // will be the function to install the default components of the Biobuzz Robot
        // again pls no

        EventBus.subscribe(CoreButton.ButtonPressEvent.class, (event) -> {
            // eventurile sunt cam niche, nu prea conteaza si nici nu (cred) ca ajuta la looptime-uri
            // practic eventurile trimit un semnal atunci cand ele se intampla iar acel semnal e interceptat in mai multe locuri
            // ex: eventul de button press e interceptat, verifica daca butonul apasat e CROSS1 si atunci scrie ceva in telemetry
            if (event.button().button() == Button.CROSS1)
                coreTelemetry.addData("Salut", timer.time().getMs());
        });
    }

    public void onStart() {
        queue(command(servo, EmptyStateSet.ZERO)); // seteaza target-ul motorului la FULL aka 1 in cazul asta
    }

    public void onLoop() {
        logger.write("Loop Time",
                "%.3f ms", // formatul doar zice ca floatul sa fie afisat cu 3 zecimale
                timer.time().getMs());
    }

    public void onStop() {
        logger.write("Stopping OpMode!"); // putem avea si print debugging doar ca e nevoie de un android studio conectat la robot
    }
}