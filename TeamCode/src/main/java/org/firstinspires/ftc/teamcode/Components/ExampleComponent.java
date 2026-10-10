package org.firstinspires.ftc.teamcode.Components;

import static ro.sparktech24345.logicore.commands.BaseCommand.command;

import com.pedropathing.controllers.PIDController;

import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;

import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.hardware.CoreMotor;
import ro.sparktech24345.logicore.hardware.CoreServo;

public class ExampleComponent implements CoreModule {

    public static final CoreMotor<EmptyStateSet> rightOuttakeMotor =
            new CoreMotor<>(GlobalStorage.rightOuttakeMotorName, EmptyStateSet.ZERO);
    // BaseStateSet e placeholderul default pentru o clasa de state-uri
    public static final CoreMotor<IntakeMotorStateSet> leftIntakeMotor =
            new CoreMotor<>(GlobalStorage.leftIntakeMotorName, IntakeMotorStateSet.FULL);
    // BaseStateSet e placeholderul default pentru o clasa de state-uri
    public static final CoreServo<EmptyStateSet> angleServo =
            new CoreServo<>(GlobalStorage.angleServoName, EmptyStateSet.ZERO);
    // vezi TestTeleOP pentru un exemplu de clasa de state-uri
    public CoreOpMode instance = null;


    /**
     * Called once during OpMode initialization - set up hardware and initial state
     */
    public void initCore() {
        instance = CoreOpMode.instance();
        instance.install(rightOuttakeMotor, 1.0); // priority reprezinta nr de ordine in care se da update la componenta
        instance.install(leftIntakeMotor, 1.0); // priority reprezinta nr de ordine in care se da update la componenta
        instance.install(angleServo, 2.0); // un priority mai mare inseamna ca se da update mai devreme la componenta
        // ex: servo isi ia update mai devreme decat motorul pentru ca 2 > 1
        leftIntakeMotor.loop((motor, target) -> target); // loop este functia f(x) : (-inf, +inf) -> [-1, 1]
        // adica ia un target si returneaza puterea data la motor ca sa se ajunga la target
        // in cazul asta parametrul _ reprezinta instanta motorului, iar functia returneaza acelasi target dat, adica practic functia este f(x) = x
    }

    /**
     * Called every loop cycle - update module logic
     */
    public void loopCore() {
        PIDController pid = new PIDController(.0, .0, .0);
        if (System.currentTimeMillis() % 2 == 0) {
            leftIntakeMotor.loop((motor, target) -> pid.calculate(target, 0.0));
        } else {
            leftIntakeMotor.loop((motor, target) -> target * 0.5);
        }

        instance.queue(command(angleServo, EmptyStateSet.ZERO)); // asa setezi target-ul motorului care ti se da in functia de customLoop
    }
}