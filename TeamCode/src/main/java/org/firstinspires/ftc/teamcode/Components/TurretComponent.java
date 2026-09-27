package org.firstinspires.ftc.teamcode.Components;

import com.pedropathing.controllers.PIDController;
import static org.firstinspires.ftc.teamcode.Components.Configs.exampleMotor;
import ro.sparktech24345.logicore.commands.StateCommand;
import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.hardware.CoreMotor;
import ro.sparktech24345.logicore.hardware.CoreServo;
import ro.sparktech24345.logicore.states.BaseStateSet;

public class TurretComponent implements CoreModule {

    public static final CoreMotor<BaseStateSet> rightOuttakeMotor =
            new CoreMotor<>("rightouttakemotor", new BaseStateSet());
                // BaseStateSet e placeholderul default pentru o clasa de state-uri
    public static final CoreMotor<BaseStateSet> leftOuttakeMotor =
                        new CoreMotor<>("leftouttakemotor", new BaseStateSet());
                // BaseStateSet e placeholderul default pentru o clasa de state-uri
    public static final CoreServo<BaseStateSet> angleServo =
                        new CoreServo<>("angleservo", new BaseStateSet());
                        // vezi TestTeleOP pentru un exemplu de clasa de state-uri
    public CoreOpMode instance = null;


    /** Called once during OpMode initialization - set up hardware and initial state */
    public void initCore() {
        instance = CoreOpMode.getInstance();
        instance.install(Hubs.CONTROL, rightOuttakeMotor, 1.0); // priority reprezinta nr de ordine in care se da update la componenta
        instance.install(Hubs.CONTROL, leftOuttakeMotor, 1.0); // priority reprezinta nr de ordine in care se da update la componenta
        instance.install(Hubs.CONTROL, angleServo, 2.0); // un priority mai mare inseamna ca se da update mai devreme la componenta
                                                    // ex: servo isi ia update mai devreme decat motorul pentru ca 2 > 1
        exampleMotor.loop((motor, target) -> target); // loop este functia f(x) : (-inf, +inf) -> [-1, 1]
                                                   // adica ia un target si returneaza puterea data la motor ca sa se ajunga la target
                                                   // in cazul asta parametrul _ reprezinta instanta motorului, iar functia returneaza acelasi target dat, adica practic functia este f(x) = x
    }

    /** Called every loop cycle - update module logic */
    public void loopCore() {
        PIDController pid = new PIDController(.0, .0, .0);
        if (System.currentTimeMillis() % 2 == 0) {
            exampleMotor.loop((motor, target) -> pid.calculate(target, .0));
        } else {
            exampleMotor.loop((motor, target) -> target * .5);
        }

        instance.queue(new StateCommand(exampleMotor.getStates().ZERO)); // asa setezi target-ul motorului care ti se da in functia de customLoop
    }
}