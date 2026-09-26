package ro.sparktech24345.logicore.pedro;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.HardwareMap;

public interface FollowerConstants {
    Follower create(HardwareMap map);
    default double getVelocityConstraint() { return 4; }
}