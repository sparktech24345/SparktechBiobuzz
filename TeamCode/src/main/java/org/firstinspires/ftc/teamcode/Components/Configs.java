package org.firstinspires.ftc.teamcode.Components;

import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;
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
            opModeConfig.performanceEngine.set(PerformanceEngine.NONE);
            opModeConfig.useFollower.set(true);
            opModeConfig.useDriveTrain.set(true);
            opModeConfig.accelerateMotors.set(true);
            opModeConfig.followerConstants.set(new ConstantsDecode());
            opModeConfig.configSetup.set(() -> {
                ConfigMap.set(GlobalStorage.frontLeftMotorName, new HardwareConfig(Hubs.EXPANSION, -1));
                ConfigMap.set(GlobalStorage.backLeftMotorName, new HardwareConfig(Hubs.EXPANSION, -1));
                ConfigMap.set(GlobalStorage.backRightMotorName, new HardwareConfig(Hubs.EXPANSION, -1));
                ConfigMap.set(GlobalStorage.frontRightMotorName, new HardwareConfig(Hubs.EXPANSION, -1));

                ConfigMap.set(GlobalStorage.intakeMotorName, new HardwareConfig(Hubs.CONTROL, -1));
                ConfigMap.set(GlobalStorage.turretRotationMotorName, new HardwareConfig(Hubs.CONTROL, -1));
                ConfigMap.set(GlobalStorage.turretFlyWheelMotorLeftName, new HardwareConfig(Hubs.CONTROL, -1));
                ConfigMap.set(GlobalStorage.turretFlyWheelMotorRightName, new HardwareConfig(Hubs.CONTROL, -1));
            });
        });

        // motors

        public static final CoreMotor<TestTeleOP.MotorTestStateSet> exampleMotor =
                new CoreMotor<>("motorleft", new TestTeleOP.MotorTestStateSet());

        // pls don't put static final motors/components and don't make a universal installBot method


        // components


        //install components

        public static void installBot() {
            CoreOpMode instance = CoreOpMode.getInstance();
            // instance.install(Hubs.CONTROL, exampleMotor, 1.0);
        }
}