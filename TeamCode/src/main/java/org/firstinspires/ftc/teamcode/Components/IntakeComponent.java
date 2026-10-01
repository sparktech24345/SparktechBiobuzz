package org.firstinspires.ftc.teamcode.Components;

import static org.firstinspires.ftc.teamcode.Components.Configs.exampleMotor;
import com.pedropathing.controllers.PIDController;
import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;
import ro.sparktech24345.logicore.commands.StateCommand;
import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.hardware.CoreMotor;
import ro.sparktech24345.logicore.hardware.CoreServo;
import ro.sparktech24345.logicore.states.BaseStateSet;
import ro.sparktech24345.logicore.states.CoreState;
import ro.sparktech24345.logicore.states.HasStates;

public class IntakeComponent<T extends BaseStateSet> implements CoreModule, HasStates<T> {

    public final CoreMotor<BaseStateSet> intakeMotor =
            new CoreMotor<>(GlobalStorage.intakeMotorName, new BaseStateSet());
    // BaseStateSet e placeholderul default pentru o clasa de state-uri
    public CoreOpMode instance = null;

    public IntakeComponent(T states) {
        this.states = states;
        states.own(this);
    }


    /** Called once during OpMode initialization - set up hardware and initial state */
    public void initCore() {
        instance = CoreOpMode.getInstance();
        instance.install(Hubs.CONTROL, intakeMotor, 1);
//        exampleMotor.loop((motor, target) -> target); // loop este functia f(x) : (-inf, +inf) -> [-1, 1]
        // adica ia un target si returneaza puterea data la motor ca sa se ajunga la target
        // in cazul asta parametrul _ reprezinta instanta motorului, iar functia returneaza acelasi target dat, adica practic functia este f(x) = x
    }

    /** Called every loop cycle - update module logic */
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

    private T states;

    public static class IntakeMotorStateSet extends BaseStateSet {
        public IntakeMotorStateSet() { super(); }
        public final CoreState FULL = register(new CoreState(1, "FULL"));
        // register() face ca state-ul sa fie detinut de motorul care foloseste StateSet-ul
        public final CoreState HALF = register(new CoreState( .5, "HALF"));
        // adica state-ul poate sa tina minte ownerul state-ului
        public final CoreState HALF_REVERSED = register(new CoreState(-0.5, "HALF_REVERSED"));
        public final CoreState FULL_REVERSED = register(new CoreState(-1, "FULL_REVERSED"));
    }
    @Override
    public T getStates() {
        return states;
    }

    @Override
    public <S extends CoreState> void setState(S state) {
        intakeMotor.setState(state);
    }
}