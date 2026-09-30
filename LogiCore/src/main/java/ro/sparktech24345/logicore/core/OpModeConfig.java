package ro.sparktech24345.logicore.core;

import com.pedropathing.config.ConfigVar;
import com.pedropathing.config.Configuration;
import com.pedropathing.math.Pose;

import ro.sparktech24345.logicore.pedro.FollowerConstants;

public class OpModeConfig {
    public final ConfigVar<OpModeType> type = ConfigVar.required();
    public final ConfigVar<FollowerConstants> followerConstants = ConfigVar.required();
    public final ConfigVar<PerformanceEngine> performanceEngine = ConfigVar.required();
    public final ConfigVar<Boolean> useDriveTrain = ConfigVar.of(true);
    public final ConfigVar<Boolean> useFollower = ConfigVar.of(true);
    public final ConfigVar<Boolean> useVoltageSensor = ConfigVar.of(true);
    public final ConfigVar<Boolean> accelerateMotors = ConfigVar.of(false);
    public final ConfigVar<Runnable> configSetup = ConfigVar.of(() -> {});
    public final ConfigVar<Pose> startPose = ConfigVar.of(Pose.zero());

    public OpModeConfig(Configuration<OpModeConfig> config) {
        config.configure(this);
    }
}