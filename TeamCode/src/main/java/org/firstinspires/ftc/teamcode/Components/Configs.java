package org.firstinspires.ftc.teamcode.Components;

import org.firstinspires.ftc.teamcode.Pedro.ConstantsDecode;

import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.core.OpModeConfig;
import ro.sparktech24345.logicore.core.OpModeType;
import ro.sparktech24345.logicore.core.PerformanceEngine;

public class Configs {
    public static final OpModeConfig teleopCfg = new OpModeConfig((opModeConfig) -> {
        opModeConfig.type.set(OpModeType.TELEOP);
        opModeConfig.performanceEngine.set(PerformanceEngine.PHOTON);
        opModeConfig.useFollower.set(true);
        opModeConfig.useDriveTrain.set(true);
        opModeConfig.accelerateMotors.set(true);
        opModeConfig.followerConstants.set(new ConstantsDecode());
    });

    //install components

    public static void installBot() {
        CoreOpMode instance = CoreOpMode.instance();
        // instance.install(Hubs.CONTROL, exampleMotor, 1.0);
    }
}