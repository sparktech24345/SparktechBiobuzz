package org.firstinspires.ftc.teamcode.Components

import com.pedropathing.controllers.PIDController
import org.firstinspires.ftc.teamcode.Components.Configs.Companion.exampleMotor
import ro.sparktech24345.logicore.commands.StateCommand
import ro.sparktech24345.logicore.config.Hubs
import ro.sparktech24345.logicore.core.CoreModule
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.hardware.CoreMotor
import ro.sparktech24345.logicore.hardware.CoreServo
import ro.sparktech24345.logicore.states.BaseStateSet

class TurretComponent: CoreModule {

    lateinit var rightOuttakeMotor: CoreMotor<BaseStateSet> // BaseStateSet e placeholderul default pentru o clasa de state-uri
    lateinit var leftOuttakeMotor: CoreMotor<BaseStateSet> // BaseStateSet e placeholderul default pentru o clasa de state-uri
    lateinit var angleServo: CoreServo<BaseStateSet> // vezi TestTeleOP pentru un exemplu de clasa de state-uri
    lateinit var instance: CoreOpMode


    /** Called once during OpMode initialization - set up hardware and initial state */
    override fun initCore() {
        instance = CoreOpMode.instance!!
        rightOuttakeMotor = instance.install(Hubs.CONTROL,CoreMotor("rightouttakemotor", BaseStateSet()), 1.0F) // priority reprezinta nr de ordine in care se da update la componenta
        leftOuttakeMotor = instance.install(Hubs.CONTROL,CoreMotor("sample_motor", BaseStateSet()), 1.0F) // priority reprezinta nr de ordine in care se da update la componenta
        angleServo = instance.install(Hubs.CONTROL,CoreServo("sample_servo", BaseStateSet()), 2.0F) // un priority mai mare inseamna ca se da update mai devreme la componenta
                                                                                                                       // ex: servo isi ia update mai devreme decat motorul pentru ca 2 > 1

        exampleMotor.customLoop = { _, target -> target } // custom loop este functia f(x) : (-inf, +inf) -> [-1, 1]
                                                   // adica ia un target si returneaza puterea data la motor ca sa se ajunga la target
                                                   // in cazul asta parametrul _ reprezinta instanta motorului, iar functia returneaza acelasi target dat, adica practic functia este f(x) = x
    }

    /** Called every loop cycle - update module logic */
    override fun loopCore() {
        val pid = PIDController(.0, .0, .0)
        if (System.currentTimeMillis() % 2L == 0L) {
            exampleMotor.customLoop = { _, target -> pid.calculate(target, .0) }
        } else {
            exampleMotor.customLoop = { _, target -> target * .5 }
        }

        instance.queue(StateCommand(exampleMotor.states.ZERO)) // asa setezi target-ul motorului care ti se da in functia de customLoop
    }
}