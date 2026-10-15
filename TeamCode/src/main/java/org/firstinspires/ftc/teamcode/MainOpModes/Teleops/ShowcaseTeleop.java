package org.firstinspires.ftc.teamcode.MainOpModes.Teleops;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Components.Configs;
import org.firstinspires.ftc.teamcode.Components.DecodeTempForTests.TiltComponent;
import org.firstinspires.ftc.teamcode.Components.DecodeTempForTests.TiltStateSet;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import ro.sparktech24345.logicore.commands.DelayCommand;
import ro.sparktech24345.logicore.commands.SequenceCommand;
import ro.sparktech24345.logicore.commands.StateCommand;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.core.CoreQueuer;
import ro.sparktech24345.logicore.utils.Benchmark;
import ro.sparktech24345.logicore.utils.PreciseTimer;
import ro.sparktech24345.logicore.utils.TimeSpec;

@TeleOp(name = "Showcase TeleOP", group = "AAA")
public class ShowcaseTeleop extends CoreOpMode {
    TiltComponent tiltComponent;
    public ShowcaseTeleop() {
        super(Configs.teleopCfg);
    }


    private final PreciseTimer mainTimer = new PreciseTimer();

    public void onInit() {
        tiltComponent = new TiltComponent();
        install(tiltComponent,1);
    }

    /**
     * TO DO
     * 1. rezolvat ca nu trimite la dashboard -- check
     * 2. Cu blaze pare ca avem niste probleme -- check
     * 3. OMA GAD KOTLIN E ASA ANNOYING        -- skill issue
     * 4. start la tickere pentru modelulele care se intampla separat decalat ex am 2 module care executa odata la 3 secunde, unul incepe la 0 celalalt la +2 -- check
     */
    public void onStart() {
        mainTimer.start();
    }

    public void onLoop() {
        int a = 0;
        Benchmark.start("b1");
        Benchmark.start("b2");
        Benchmark.start("b3");
        Benchmark.start("b4");
        a += 1;
        Benchmark.end();
        Benchmark.end();
        Benchmark.end();
        Benchmark.end();



        if(gamepad1.aWasPressed()){
            execute(new SequenceCommand(new Consumer<CoreQueuer>() {
                @Override
                public void accept(CoreQueuer coreQueuer) {
                    coreQueuer.queue(new StateCommand<>(tiltComponent,TiltStateSet.INACTIVE));
                    coreQueuer.queue(new DelayCommand(TimeSpec.fromMillis(20)));
                    coreQueuer.queue(new StateCommand<>(tiltComponent,TiltStateSet.ACTIVE));
                }
            }
            ));
        }


        coreTelemetry.addData("voltage", voltageSensor.voltage());
        coreTelemetry.addData("pose", coreFollower.pose());
        coreTelemetry.addData("Loop time", mainTimer.timeMs());
//        coreTelemetry.addData("pos", getFollower().pose());
        mainTimer.start();
    }
}