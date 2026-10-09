package org.firstinspires.ftc.teamcode.Components;

import org.firstinspires.ftc.teamcode.Helpers.GlobalStorage;
import org.firstinspires.ftc.teamcode.Pedro.ConstantsDecode;
import ro.sparktech24345.logicore.config.MotorMap;
import ro.sparktech24345.logicore.config.Keys;
import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.core.OpModeConfig;
import ro.sparktech24345.logicore.core.OpModeType;
import ro.sparktech24345.logicore.core.PerformanceEngine;

public class ConfigsDecode {
        public static final OpModeConfig decodeCfg = new OpModeConfig((opModeConfig) -> {
            opModeConfig.type.set(OpModeType.TELEOP);
            opModeConfig.performanceEngine.set(PerformanceEngine.PHOTON);
            opModeConfig.useFollower.set(true);
            opModeConfig.useDriveTrain.set(true);
            opModeConfig.accelerateMotors.set(true);
            opModeConfig.followerConstants.set(new ConstantsDecode());
        });
}