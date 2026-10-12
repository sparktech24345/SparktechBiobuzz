package ro.sparktech24345.logicore.core;

import com.pedropathing.config.ConfigVar;
import com.pedropathing.config.Configuration;
import com.pedropathing.math.Pose;

import java.io.PrintStream;

import ro.sparktech24345.logicore.pedro.FollowerConstants;

public class OpModeConfig {
    public final ConfigVar<OpModeType> type = ConfigVar.required();
    public final ConfigVar<FollowerConstants> followerConstants = ConfigVar.required();
    public final ConfigVar<PerformanceEngine> performanceEngine = ConfigVar.required();
    public final ConfigVar<PrintStream> fd = ConfigVar.of(System.out);
    /** Install the gamepad DriveTrain module (needed for DriveMode.GAMEPAD). */
    public final ConfigVar<Boolean> useDriveTrain = ConfigVar.of(true);
    /** Who drives when the OpMode starts. Can be switched later with CoreOpMode.driveMode(...). */
    public final ConfigVar<DriveMode> driveMode = ConfigVar.of(DriveMode.GAMEPAD);
    public final ConfigVar<Boolean> useFollower = ConfigVar.of(true);
    public final ConfigVar<Boolean> useVoltageSensor = ConfigVar.of(true);
    public final ConfigVar<Boolean> accelerateMotors = ConfigVar.of(false);
    public final ConfigVar<Pose> startPose = ConfigVar.of(Pose.zero());
    /** Enables Benchmark timings and per-loop stage logging (slow: prints to logcat every loop). */
    public final ConfigVar<Boolean> debug = ConfigVar.of(false);

    public OpModeConfig(Configuration<OpModeConfig> config) {
        config.configure(this);
    }
}