package org.firstinspires.ftc.teamcode.Components;

import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.hardware.Controllers.VPIDController;
import ro.sparktech24345.logicore.hardware.CoreMotor;
import ro.sparktech24345.logicore.hardware.CoreServo;

public class TurretComponent implements CoreModule {
    public VPIDController vpidController = new VPIDController(0,0,0,0,0);
    public TurretComponent setConstants(double p, double i, double d, double f, double s) {
        vpidController.setConstants(p,i,d,f,s);
        return this;
    }

    public static final CoreMotor<EmptyStateSet> rightOuttakeMotor =
            new CoreMotor<>(GlobalStorage.rightOuttakeMotorName, EmptyStateSet.ZERO);
                // BaseStateSet e placeholderul default pentru o clasa de state-uri
    public static final CoreMotor<EmptyStateSet> leftOuttakeMotor =
                        new CoreMotor<>(GlobalStorage.leftOuttakeMotorName, EmptyStateSet.ZERO);
                // BaseStateSet e placeholderul default pentru o clasa de state-uri
    public static final CoreServo<EmptyStateSet> angleServo =
                        new CoreServo<>(GlobalStorage.angleServoName, EmptyStateSet.ZERO);
                        // vezi TestTeleOP pentru un exemplu de clasa de state-uri
    public CoreOpMode instance = null;


    /** Called once during OpMode initialization - set up hardware and initial state */
    public void initCore() {
        instance = CoreOpMode.instance();
        instance.install(rightOuttakeMotor, 1.0); // priority reprezinta nr de ordine in care se da update la componenta
        instance.install(leftOuttakeMotor, 1.0); // priority reprezinta nr de ordine in care se da update la componenta
        instance.install(angleServo, 2.0); // un priority mai mare inseamna ca se da update mai devreme la componenta
                                                    // ex: servo isi ia update mai devreme decat motorul pentru ca 2 > 1

        angleServo.initCore(); /// might be already initialized from the installation
        rightOuttakeMotor.initCore();
        leftOuttakeMotor.initCore();

        rightOuttakeMotor.encoded(true);
        leftOuttakeMotor.encoded(false);


        rightOuttakeMotor.loop((motor, target) -> vpidController.calculate(target, rightOuttakeMotor.velocity()));
        }

    /** Called every loop cycle - update module logic */
    public void loopCore() {
        rightOuttakeMotor.loopCore();
        leftOuttakeMotor.motorPower(rightOuttakeMotor.motorPower());
        leftOuttakeMotor.loopCore();
        angleServo.loopCore();


//        instance.queue(command(exampleMotor.getStates().DEFAULT)); // asa setezi target-ul motorului care ti se da in functia de customLoop
    }

    /// now how tf do I make some states for this turret to be on / off in an easy built in way
}