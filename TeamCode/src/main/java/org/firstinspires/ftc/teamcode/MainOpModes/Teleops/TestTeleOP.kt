package org.firstinspires.ftc.teamcode.MainOpModes.Teleops

import com.pedropathing.drivetrain.DrivePowers
import com.pedropathing.math.Pose
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.MyConstants
import org.firstinspires.ftc.teamcode.Subsystems.TurretComponent
import ro.sparktech24345.logicore.commands.StateCommand
import ro.sparktech24345.logicore.core.CoreButton
import ro.sparktech24345.logicore.core.CoreGamepad.Button
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.events.EventBus
import ro.sparktech24345.logicore.hardware.CoreMotor
import ro.sparktech24345.logicore.hardware.CoreServo
import ro.sparktech24345.logicore.states.BaseStateSet
import ro.sparktech24345.logicore.states.CoreState
import ro.sparktech24345.logicore.utils.PreciseTimer

@TeleOp(name = "Test Op Mode", group = "Testing")
class TestTeleOP: CoreOpMode(OpModeType.TESTING, // sau AUTONOMOUS sau TELEOP, pretty self explanatory
    followerConstants = MyConstants(), // constantele pt pedro, trebuie sa fie o clasa ce da implement la FollowerConstants
    performanceEngine = PerformanceEngine.PHOTON // sau BLAZE sau NONE, also pretty self explanatory
) {

    class MotorTestStateSet: BaseStateSet() {
        val FULL = register(CoreState(1.0, "FULL")) // register() face ca state-ul sa fie detinut de motorul care foloseste StateSet-ul
        val HALF = register(CoreState( .5, "HALF")) // adica state-ul dupa ce e registered poate sa isi ia own() de la motor ca apoi sa tina minte ownerul state-ului
    }

    val timer = PreciseTimer()

    lateinit var motor: CoreMotor<MotorTestStateSet>
    lateinit var servo: CoreServo<BaseStateSet>
    lateinit var turret: TurretComponent

    private var showMessage = false

    override fun onInit() {
        timer.start() // doar reseteaza timerul
        motor = cInstall(CoreMotor("motorleft", MotorTestStateSet()), 1.0F) // cInstall - instaleaza module specifice control hub-ului
        servo = eInstall(CoreServo("servo_sample_name", BaseStateSet()), 1.0F) // eInstall - instaleaza module specifice expansion hub-ului
        turret = iInstall(TurretComponent(), 1.0F)                                           // iInstall - instaleaza module ce nu depind de un hub
                                                                                                            // am facut asta pt ca e important la bulk readuri sa citesti hub-urile pe rand

        EventBus.subscribe(CoreButton.ButtonPressEvent::class.java) { // eventurile sunt cam niche care nu prea conteaza si nici nu (cred) ca ajuta la looptime-uri
                event -> event.button                                        // practic eventurile trimit un semnal atunci cand ele se intampla iar acel semnal e interceptat in mai multe locuri
            if (event.button == gamepad[Button.CROSS1]) showMessage != showMessage // ex: eventul de button press e interceptat, verifica daca butonul apasat e CROSS1 si atunci da toggle la showMessage
        }
    }

    override fun onStart() {
        queue(StateCommand(motor.states.FULL)) // seteaza target-ul motorului la FULL aka 1 in cazul asta
    }

    override fun onLoop() {
        telemetry.addData("Loop Time",
            "%.3f ms", // formatul doar zice ca floatul sa fie afisat cu 3 zecimale
            timer.getTime().get(PreciseTimer.TimeUnit.MILLIS)) // getTime() returneaza o peroiada de timp (TimeSpec) si get() da valoarea numerica la scara preferata (mili, nano, sec etc.)
        if (showMessage) telemetry.addLine("Secret message!")
    }

    override fun onStop() {
        println("Stopping OpMode!") // putem avea si print debugging doar ca e nevoie de un android studio conectat la robot
    }
}