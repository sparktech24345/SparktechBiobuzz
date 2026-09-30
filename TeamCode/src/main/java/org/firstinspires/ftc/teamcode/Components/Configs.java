package org.firstinspires.ftc.teamcode.Components;

import org.firstinspires.ftc.teamcode.MainOpModes.Teleops.TestTeleOP;
import org.firstinspires.ftc.teamcode.Pedro.ConstantsDecode;
import ro.sparktech24345.logicore.config.ConfigMap;
import ro.sparktech24345.logicore.config.HardwareConfig;
import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.core.OpModeConfig;
import ro.sparktech24345.logicore.core.OpModeType;
import ro.sparktech24345.logicore.core.PerformanceEngine;
import ro.sparktech24345.logicore.hardware.CoreMotor;

public class Configs {
        public static final OpModeConfig teleopCfg = new OpModeConfig((opModeConfig) -> {
            opModeConfig.type.set(OpModeType.TELEOP);
            opModeConfig.performanceEngine.set(PerformanceEngine.BLAZE);
            opModeConfig.useFollower.set(true);
            opModeConfig.useDriveTrain.set(true);
            opModeConfig.accelerateMotors.set(true);
            opModeConfig.followerConstants.set(new ConstantsDecode());
            opModeConfig.configSetup.set(() -> {
                ConfigMap.set("frontleft", new HardwareConfig(Hubs.EXPANSION, -1));
                ConfigMap.set("backleft", new HardwareConfig(Hubs.EXPANSION, -1));
                ConfigMap.set("backright", new HardwareConfig(Hubs.EXPANSION, -1));
                ConfigMap.set("frontright", new HardwareConfig(Hubs.EXPANSION, -1));

                ConfigMap.set("rightintakemotor", new HardwareConfig(Hubs.CONTROL, -1));
                ConfigMap.set("leftintakemotor", new HardwareConfig(Hubs.CONTROL, -1));

                ConfigMap.set("rightouttakemotor", new HardwareConfig(Hubs.CONTROL, -1));
                ConfigMap.set("leftouttakemotor", new HardwareConfig(Hubs.CONTROL, -1));
            });

        });

        // motors

        public static final CoreMotor<TestTeleOP.MotorTestStateSet> exampleMotor =
                new CoreMotor<>("motorleft", new TestTeleOP.MotorTestStateSet());
                    // pls don't do this, do it like TurretComponent


        // components


        //install components

        public static void installBot() {
            CoreOpMode instance = CoreOpMode.getInstance();
            // instance.install(Hubs.CONTROL, exampleMotor, 1.0);
        }
}