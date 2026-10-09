package org.firstinspires.ftc.teamcode.Components;

import android.util.Pair;

import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;

import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.hardware.CoreMotor;
import ro.sparktech24345.logicore.hardware.CoreServo;
import ro.sparktech24345.logicore.states.HasStates;

public class IntakeComponent implements CoreModule, HasStates<Pair<IntakeMotorStateSet, CoupleServoStateSet>, IntakeStateSet> {


    public final CoreServo<CoupleServoStateSet> coupleServo;
    public final CoreMotor<IntakeMotorStateSet> intakeMotor;
    private CoreOpMode instance = null;

    public IntakeComponent() {
        intakeMotor = new CoreMotor<>(GlobalStorage.intakeMotorName, IntakeMotorStateSet.ZERO);
        coupleServo = new CoreServo<>(GlobalStorage.coupleServo, CoupleServoStateSet.COUPLED);
        this.states = IntakeStateSet.class;
    }


    /**
     * Called once during OpMode initialization - set up hardware and initial state
     */
    public void initCore() {
        instance = CoreOpMode.instance();
        instance.install(intakeMotor, 1);
        instance.install(coupleServo, 1);


//        exampleMotor.loop((motor, target) -> target); // loop este functia f(x) : (-inf, +inf) -> [-1, 1]
        // adica ia un target si returneaza puterea data la motor ca sa se ajunga la target
        // in cazul asta parametrul _ reprezinta instanta motorului, iar functia returneaza acelasi target dat, adica practic functia este f(x) = x
    }

    /**
     * Called every loop cycle - update module logic
     */
    public void loopCore() {
//        PIDController pid = new PIDController(.0, .0, .0);
//        if (System.currentTimeMillis() % 2 == 0) {
//            exampleMotor.loop((motor, target) -> pid.calculate(target, .0));
//        } else {
//            exampleMotor.loop((motor, target) -> target * .5);
//        }
//
//        instance.queue(new StateCommand(exampleMotor.getStates().ZERO)); // asa setezi target-ul motorului care ti se da in functia de customLoop
    }

    private final Class<IntakeStateSet> states;

    @Override
    public Class<IntakeStateSet> states() {
        return states;
    }

    private IntakeStateSet currState = null;

    @Override
    public IntakeStateSet state() {
        return currState;
    }

    @Override
    public void state(IntakeStateSet state) {
        intakeMotor.state(state.value().first);
        coupleServo.state(state.value().second);
        currState = state;
    }
}