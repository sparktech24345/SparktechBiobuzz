package org.firstinspires.ftc.teamcode.Subsystems

import com.pedropathing.controllers.PIDController
import ro.sparktech24345.logicore.commands.StateCommand
import ro.sparktech24345.logicore.core.CoreModule
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.hardware.CoreMotor
import ro.sparktech24345.logicore.hardware.CoreServo
import ro.sparktech24345.logicore.states.BaseStateSet

class TurretComponent: CoreModule {

    lateinit var motor: CoreMotor<BaseStateSet> // BaseStateSet e placeholderul default pentru o clasa de state-uri
    lateinit var servo: CoreServo<BaseStateSet> // vezi TestTeleOP pentru un exemplu de clasa de state-uri
    lateinit var instance: CoreOpMode


    /** Called once during OpMode initialization - set up hardware and initial state */
    override fun initCore() {
        instance = CoreOpMode.instance!!
        motor = instance.cInstall(CoreMotor("sample_motor", BaseStateSet()), 1.0F) // priority reprezinta nr de ordine in care se da update la componenta
        servo = instance.cInstall(CoreServo("sample_servo", BaseStateSet()), 2.0F) // un priority mai mare inseamna ca se da update mai devreme la componenta
                                                                                                                       // ex: servo isi ia update mai devreme decat motorul pentru ca 2 > 1

        motor.customLoop = { _, target -> target } // custom loop este functia f(x) : (-inf, +inf) -> [-1, 1]
                                                   // adica ia un target si returneaza puterea data la motor ca sa se ajunga la target
                                                   // in cazul asta parametrul _ reprezinta instanta motorului, iar functia returneaza acelasi target dat, adica practic functia este f(x) = x
    }

    /** Called every loop cycle - update module logic */
    override fun loopCore() {
        val pid = PIDController(.0, .0, .0)
        if (System.currentTimeMillis() % 2L == 0L) {
            motor.customLoop = { _, target -> pid.calculate(target, .0) }
        } else {
            motor.customLoop = { _, target -> target * .5 }
        }

        instance.queue(StateCommand(motor.states.ZERO)) // asa setezi target-ul motorului care ti se da in functia de customLoop
    }
}