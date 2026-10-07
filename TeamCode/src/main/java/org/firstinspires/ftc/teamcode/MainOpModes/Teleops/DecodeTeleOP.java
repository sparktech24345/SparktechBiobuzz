package org.firstinspires.ftc.teamcode.MainOpModes.Teleops;

import static ro.sparktech24345.logicore.commands.BaseCommand.command;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Components.Configs;
import org.firstinspires.ftc.teamcode.Components.ConfigsDecode;
import org.firstinspires.ftc.teamcode.Components.GateComponent;
import org.firstinspires.ftc.teamcode.Components.IntakeComponent;
import ro.sparktech24345.logicore.commands.StateCommand;
import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.core.Button;
import ro.sparktech24345.logicore.core.CoreButton;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.events.EventBus;
import ro.sparktech24345.logicore.utils.PreciseTimer;

@Config
@TeleOp(name = "Decode TeleOP", group = "Testing")
public class DecodeTeleOP extends CoreOpMode {

    public static boolean useEvents = false;

    public DecodeTeleOP() {
        super(ConfigsDecode.decodeCfg);
    }

//    public static class MotorTestStateSet extends BaseStateSet {
//        public MotorTestStateSet() { super(); }
//        public final CoreState FULL = register(new CoreState(1, "FULL"));
//        // register() face ca state-ul sa fie detinut de motorul care foloseste StateSet-ul
//        public final CoreState HALF = register(new CoreState( .5, "HALF"));
//        // adica state-ul poate sa tina minte ownerul state-ului
//    }

    public final PreciseTimer timer = new PreciseTimer();
//    public final CoreServo<BaseStateSet> servo =
//            new CoreServo<>("sample_servo", new BaseStateSet());
//    public final TurretComponent turret = new TurretComponent();
    public final IntakeComponent intake = new IntakeComponent();
    public final GateComponent gates = new GateComponent();

    public void onInit() {
        timer.start(); // doar reseteaza timerul
//        install(Hubs.EXPANSION, servo, 1);
//        install(Hubs.INDEPENDENT, turret, 1);
        install(Hubs.INDEPENDENT, intake, 1);
        install(Hubs.INDEPENDENT, gates, 1);
        queue(new StateCommand<>(gates.getStates().DEFAULT));
        EventBus.subscribe(CoreButton.ButtonPressEvent.class, (event) -> {
            if (useEvents) {
                switch (event.getButton().getButton()) {
                    case CIRCLE1:
                        queue(new StateCommand<>(intake.getStates().DEFAULT),
                                new StateCommand<>(gates.getStates().CLOSED));
                        break;
                    case SQUARE1:
                        queue(new StateCommand<>(intake.getStates().FULL_COUPLED),
                                new StateCommand<>(gates.getStates().LEFT_OPEN));
                        break;
                }
            }
        });



//        EventBus.subscribe(CoreButton.ButtonPressEvent.class, (event) -> {
//            CoreButton buttonInstance = event.getButton();
//            Button buttonEnum = buttonInstance.getButton();
//            switch (buttonEnum) {
//                case CROSS1: coreTelemetry.tel.addData("Hello, world!", timer.getTime().getMs()); break;
//                case TRIANGLE1: coreTelemetry.tel.addLine("Secret message!"); break;
//                default: System.out.println("got button " + buttonEnum); break;
//            }
//        });
    }

    public void onStart() {
//        queue(new StateCommand(exampleMotor.getStates().FULL)); // seteaza target-ul motorului la FULL aka 1 in cazul asta
//        queue(new StateCommand<>(intake.getStates().FULL_DECOUPLED));

    }

    public void onLoop() {
        if (!useEvents) {
            if (gamepad.get(Button.CIRCLE1).isToggled()) {
                queue(new StateCommand<>(intake.getStates().DEFAULT),
                        new StateCommand<>(gates.getStates().CLOSED));
            }
            if (gamepad.get(Button.RIGHT_BUMPER1).isToggled()) {
                queue(new StateCommand<>(intake.getStates().FULL_COUPLED),
                        new StateCommand<>(gates.getStates().LEFT_OPEN));
            }
        }

        telemetry.addData("Loop Time",
            "%.3f ms", // formatul doar zice ca floatul sa fie afisat cu 3 zecimale
            timer.getTime().getMs());
    }

    public void onStop() {
        System.out.println("Stopping OpMode!"); // putem avea si print debugging doar ca e nevoie de un android studio conectat la robot
    }
}