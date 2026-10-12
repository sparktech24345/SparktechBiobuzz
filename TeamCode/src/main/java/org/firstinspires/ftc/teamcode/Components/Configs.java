package org.firstinspires.ftc.teamcode.Components;

import org.firstinspires.ftc.teamcode.Pedro.ConstantsDecode;

import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.core.DriveMode;
import ro.sparktech24345.logicore.core.OpModeConfig;
import ro.sparktech24345.logicore.core.OpModeType;
import ro.sparktech24345.logicore.core.PerformanceEngine;

public class Configs {
    public static final OpModeConfig teleopCfg = new OpModeConfig((opModeConfig) -> {
        opModeConfig.type.set(OpModeType.TELEOP);
        opModeConfig.performanceEngine.set(PerformanceEngine.BLAZE);
        opModeConfig.useFollower.set(true);
        opModeConfig.useDriveTrain.set(true);
        opModeConfig.debug.set(true);
        opModeConfig.driveMode.set(DriveMode.GAMEPAD); // driver drives; switch with driveMode(...) for auto-aim
        opModeConfig.accelerateMotors.set(false);
        opModeConfig.followerConstants.set(new ConstantsDecode());
    });

    public static final OpModeConfig autoCfg = new OpModeConfig((opModeConfig) -> {
        opModeConfig.type.set(OpModeType.AUTONOMOUS);
        opModeConfig.performanceEngine.set(PerformanceEngine.BLAZE);
        opModeConfig.useFollower.set(true);
        opModeConfig.useDriveTrain.set(false);
        opModeConfig.driveMode.set(DriveMode.FOLLOWER); // the follower drives in auto
        opModeConfig.accelerateMotors.set(false);
        opModeConfig.followerConstants.set(new ConstantsDecode());
    });

    //install components

    public static void installBot() {
        CoreOpMode instance = CoreOpMode.instance();
        // instance.install(Hubs.CONTROL, exampleMotor, 1.0);
    }
}