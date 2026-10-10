package org.firstinspires.ftc.teamcode.MainOpModes.Teleops;


import static org.firstinspires.ftc.teamcode.Helpers.GlobalStorage.camId;
import static ro.sparktech24345.logicore.commands.BaseCommand.command;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.teamcode.Components.ConfigsDecode;
import org.firstinspires.ftc.teamcode.Components.DecodeTurretComponent;
import org.firstinspires.ftc.teamcode.Components.GateComponent;
import org.firstinspires.ftc.teamcode.Components.GateStateSet;
import org.firstinspires.ftc.teamcode.Components.IntakeComponent;
import org.firstinspires.ftc.teamcode.Components.TiltComponent;
import org.firstinspires.ftc.teamcode.Components.IntakeStateSet;
import org.firstinspires.ftc.teamcode.Helpers.Color;
import org.firstinspires.ftc.teamcode.Helpers.GetColorCase;

import dev.anygeneric.blazeftc.Hub;
import java.util.List;

import ro.sparktech24345.logicore.commands.DelayCommand;
import ro.sparktech24345.logicore.commands.StateCommand;
import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.core.Button;
import ro.sparktech24345.logicore.core.CoreButton;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.core.CoreTelemetry;
import ro.sparktech24345.logicore.events.EventBus;
import ro.sparktech24345.logicore.hardware.CoreColorSensor;
import ro.sparktech24345.logicore.hardware.CoreLimelight;
import ro.sparktech24345.logicore.hardware.CoreServo;
import ro.sparktech24345.logicore.utils.PreciseTimer;
import ro.sparktech24345.logicore.utils.TimeSpec;

import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;
import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage.*;

import java.util.List;

@Config
@TeleOp(name = "Decode TeleOP", group = "Testing")
public class DecodeTeleOP extends CoreOpMode {

    public static boolean useEvents = false;
    public final CoreColorSensor leftColorSensor = new CoreColorSensor(GlobalStorage.colorSensorLeftName);
    public final CoreColorSensor rightColorSensor = new CoreColorSensor(GlobalStorage.colorSensorRightName);
    public final CoreLimelight limelight = new CoreLimelight(GlobalStorage.limelightName);
    protected GetColorCase colorCase = GetColorCase.NOSORT;
    protected Color ballColorRight;
    protected Color ballColorLeft;

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
    public final DecodeTurretComponent decodeTurret = new DecodeTurretComponent();
    public final TiltComponent tilt = new TiltComponent();

    public void onInit() {
        timer.start(); // doar reseteaza timerul
//        install(Hubs.EXPANSION, servo, 1);
//        install(Hubs.INDEPENDENT, turret, 1);
        install(Hubs.INDEPENDENT, intake, 1);
        install(Hubs.INDEPENDENT, gates, 2);
        install(Hubs.INDEPENDENT, decodeTurret, 2);
        install(Hubs.INDEPENDENT,tilt,2);
        queue(new StateCommand<>(gates.getStates().DEFAULT));
        queue(new StateCommand<>(tilt.getStates().DEFAULT));
        if(useEvents) {
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
                        case LEFT_TRIGGER1:
                            decodeTurret.aimAt(130, 53);
                            break;
                        case LEFT_BUMPER1:
                            if(colorCase == GetColorCase.NOSORT){
                                queue(new StateCommand<>(intake.getStates().FULL_COUPLED),
                                        new StateCommand<>(gates.getStates().LEFT_OPEN),
                                        new DelayCommand(TimeSpec.fromMillis(500)),
                                        new StateCommand<>(gates.getStates().RIGHT_OPEN),
                                        new StateCommand<>(gates.getStates().CLOSED)
                                );
                            }
                            break;
                        case DPAD_UP1:
                                if(tilt.currentState() == tilt.getStates().DEFAULT) {
                                    execute(
                                            new StateCommand<>(tilt.getStates().ACTIVE)
                                    );
                                    coreTelemetry.addData("tiltState",tilt.currentState());
                                }
                                if(tilt.currentState() == tilt.getStates().ACTIVE)
                                    execute(new StateCommand<>(tilt.getStates().DEFAULT));
                                break;

                    }
                }
            });
        }



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
        handleColors();
        //useCamera();
        if (!useEvents) {
            if (gamepad.get(Button.CIRCLE1).isToggled()) {
                queue(new StateCommand<>(intake.getStates().DEFAULT),
                        new StateCommand<>(gates.getStates().CLOSED));
            }
            if (gamepad.get(Button.RIGHT_BUMPER1).isToggled()) {
                queue(new StateCommand<>(intake.getStates().FULL_COUPLED));
            }
            if (gamepad.get(Button.RIGHT_TRIGGER1).isToggled()) {
                decodeTurret.aimAt(130, 53);
            }
            if(gamepad.get(Button.DPAD_UP1).isPressed()){
                if(tilt.currentState() == tilt.getStates().DEFAULT) {
                    execute(
                            new StateCommand<>(tilt.getStates().ACTIVE)
                    );
                    coreTelemetry.addData("tiltState",tilt.currentState());
                }
                if(tilt.currentState() == tilt.getStates().ACTIVE)
                    execute(new StateCommand<>(tilt.getStates().DEFAULT));
            }
            if(gamepad.get(Button.LEFT_BUMPER1).isPressed()){ // transfer button???
                if(colorCase == GetColorCase.NOSORT){
                    queue(new StateCommand<>(intake.getStates().FULL_COUPLED),
                            new StateCommand<>(gates.getStates().LEFT_OPEN),
                            new DelayCommand(TimeSpec.fromMillis(1500)),
                            new StateCommand<>(gates.getStates().RIGHT_OPEN),
                            new DelayCommand(TimeSpec.fromMillis(1500)),
                            new StateCommand<>(gates.getStates().CLOSED)
                    );
                } else {
                    int greenBallPosition;
                    if (ballColorRight == Color.GREEN)
                        greenBallPosition = 1; // green is on the right
                    else if (ballColorLeft == Color.GREEN)
                        greenBallPosition = 2; // green is on the left
                    else greenBallPosition = 3; // green is on the right
                    if (colorCase == GetColorCase.PGP && greenBallPosition == 1)
                        greenBallPosition = 4;
                    if (colorCase == GetColorCase.PGP && greenBallPosition == 2)
                        greenBallPosition = 3;
                    if (colorCase == GetColorCase.PGP && greenBallPosition == 3)
                        greenBallPosition = 2;
                    if (colorCase == GetColorCase.GPP && greenBallPosition == 1)
                        greenBallPosition = 3;
                    if (colorCase == GetColorCase.GPP && greenBallPosition == 2)
                        greenBallPosition = 4;
                    if (colorCase == GetColorCase.GPP && greenBallPosition == 3)
                        greenBallPosition = 2;

                    switch (greenBallPosition) {
                        case 1: //llr
                            queue(new StateCommand<>(intake, IntakeStateSet.FULL_COUPLED),
                                    new StateCommand<>(gates, GateStateSet.LEFT_OPEN),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.CLOSED),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.LEFT_OPEN),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.RIGHT_OPEN),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.CLOSED)
                            );
                            break;
                        case 2: //rrl
                            queue(new StateCommand<>(intake, IntakeStateSet.FULL_COUPLED),
                                    new StateCommand<>(gates, GateStateSet.RIGHT_OPEN),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.CLOSED),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.RIGHT_OPEN),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.LEFT_OPEN),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.CLOSED)
                            );
                            break;
                        case 3: //rlr
                            queue(new StateCommand<>(intake, IntakeStateSet.FULL_COUPLED),
                                    new StateCommand<>(gates, GateStateSet.RIGHT_OPEN),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.CLOSED),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.LEFT_OPEN),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.RIGHT_OPEN),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.CLOSED)
                            );
                            break;
                        case 4: //lrl
                            queue(new StateCommand<>(intake, IntakeStateSet.FULL_COUPLED),
                                    new StateCommand<>(gates, GateStateSet.LEFT_OPEN),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates, GateStateSet.CLOSED),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates.GateStateSet.RIGHT_OPEN),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates.GateStateSet.LEFT_OPEN),
                                    new DelayCommand(TimeSpec.fromMillis(100)),
                                    new StateCommand<>(gates.GateStateSet.CLOSED)
                            );
                    }
                }
            }
        }

        coreTelemetry.addData("Loop Time", // formatul doar zice ca floatul sa fie afisat cu 3 zecimale
            timer.getTime().getMs());

        coreTelemetry.addData("Robot Pose", CoreOpMode.getInstance().getFollower().pose());
        coreTelemetry.addData("Robot X", CoreOpMode.getInstance().getFollower().pose().x());
        coreTelemetry.addData("Robot Y", CoreOpMode.getInstance().getFollower().pose().y());
        coreTelemetry.addData("Robot heading", CoreOpMode.getInstance().getFollower().pose().heading());
        coreTelemetry.addData("ballcolorleft",ballColorLeft);
        coreTelemetry.addData("ballcolorright",ballColorRight);
        coreTelemetry.addData("leftred",leftColorSensor.r());
        coreTelemetry.addData("leftgreen",leftColorSensor.g());
        coreTelemetry.addData("leftblue",leftColorSensor.b());
        coreTelemetry.addData("rightred",rightColorSensor.r());
        coreTelemetry.addData("rightgreen",rightColorSensor.g());
        coreTelemetry.addData("rightblue",rightColorSensor.b());
        coreTelemetry.addData("error",DecodeTurretComponent.error);

    }
    protected void handleColors(){
        ballColorLeft = Color.getColorForStorage(leftColorSensor.r(),leftColorSensor.g(),leftColorSensor.b());
        ballColorRight = Color.getColorForStorage(rightColorSensor.r(),rightColorSensor.g(),rightColorSensor.b());
    }
    public void useCamera(){
        limelight.setPipeline(2);

        LLResult llResult = limelight.getResult();
        List<LLResultTypes.FiducialResult> fiducialResults = llResult.getFiducialResults();
        for (LLResultTypes.FiducialResult fr : fiducialResults) {
        camId = fr.getFiducialId();
        }
        if(camId < 21 || camId > 23) camId = 23;
        colorCase = GetColorCase.getCase();
    }
    public void onStop() {
        System.out.println("Stopping OpMode!"); // putem avea si print debugging doar ca e nevoie de un android studio conectat la robot
    }
}