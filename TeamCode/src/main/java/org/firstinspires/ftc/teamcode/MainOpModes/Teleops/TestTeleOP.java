package org.firstinspires.ftc.teamcode.MainOpModes.Teleops;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Components.Configs;
import static org.firstinspires.ftc.teamcode.Components.Configs.exampleMotor;
import static org.firstinspires.ftc.teamcode.Components.Configs.installBot;
import static ro.sparktech24345.logicore.commands.BaseCommand.command;

import org.firstinspires.ftc.teamcode.Components.TurretComponent;
import ro.sparktech24345.logicore.commands.StateCommand;
import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.core.CoreButton;
import ro.sparktech24345.logicore.core.Button;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.events.EventBus;
import ro.sparktech24345.logicore.hardware.CoreServo;
import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;
import ro.sparktech24345.logicore.utils.PreciseTimer;

@TeleOp(name = "Test Op Mode", group = "Testing")
public class TestTeleOP extends CoreOpMode {
    public TestTeleOP() {
        super(Configs.teleopCfg);
    }

    public static class MotorTestStateSet extends BaseStateSet<Double> {
        public MotorTestStateSet() { super(); }
        public final CoreState<Double> ZERO = state(.0, "ZERO");
        public final CoreState<Double> DEFAULT = ZERO;
        public final CoreState<Double> FULL = state(1.0, "FULL");
        // register() face ca state-ul sa fie detinut de motorul care foloseste StateSet-ul
        public final CoreState<Double> HALF = state( .5, "HALF");
        // adica state-ul poate sa tina minte ownerul state-ului
    }

    public final PreciseTimer timer = new PreciseTimer();
    public final CoreServo<BaseStateSet<Double>> servo =
            new CoreServo<>("sample_servo", new BaseStateSet<>());
    public final TurretComponent turret = new TurretComponent();

    public void onInit() {
        timer.start(); // doar reseteaza timerul
        install(Hubs.EXPANSION, servo, 1);
        install(Hubs.INDEPENDENT, turret, 1);

        installBot(); // will be the function to install the default components of the Biobuzz Robot
                      // again pls no

        EventBus.subscribe(CoreButton.ButtonPressEvent.class, (event) -> {
            // eventurile sunt cam niche, nu prea conteaza si nici nu (cred) ca ajuta la looptime-uri
            // practic eventurile trimit un semnal atunci cand ele se intampla iar acel semnal e interceptat in mai multe locuri
            // ex: eventul de button press e interceptat, verifica daca butonul apasat e CROSS1 si atunci scrie ceva in telemetry
            if (event.getButton().getButton() == Button.CROSS1)
                coreTelemetry.tel.addData("Salut", timer.getTime().getMs());
        });
    }

    public void onStart() {
        queue(command(exampleMotor.getStates().FULL)); // seteaza target-ul motorului la FULL aka 1 in cazul asta
    }

    public void onLoop() {
        telemetry.addData("Loop Time",
            "%.3f ms", // formatul doar zice ca floatul sa fie afisat cu 3 zecimale
            timer.getTime().getMs());
    }

    public void onStop() {
        System.out.println("Stopping OpMode!"); // putem avea si print debugging doar ca e nevoie de un android studio conectat la robot
    }
}