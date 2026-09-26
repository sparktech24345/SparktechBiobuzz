package ro.sparktech24345.logicore.pedro;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import ro.sparktech24345.logicore.core.CoreModule;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.curves.Curve;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.utils.Benchmark;

public class CoreFollower<T extends FollowerConstants> implements CoreModule {
    // private val constants: NeededConstants,
    // val startPose: Pose = Pose.zero(),
    // val poseFactory: PoseFactory = PoseFactory.degrees(),
    // private val initWithLastPose: Boolean = false

    private final T constants;
    private final Pose startPose;

    public CoreFollower(T constants) {
        this(constants, null);
    }
    public CoreFollower(T constants, Pose startPose) {
        this.constants = constants;
        this.startPose = startPose == null ? PoseStorage.lastPose : startPose;

    }

    private Follower follower;
    public Follower follower() { return follower; }
    public void pose(Pose pose) { follower.setPose(pose); }
    public Pose pose() { return follower.pose(); }
    public double progress() { return follower.completion(); }
    public double distanceLeft() { return follower.remainingDistance(); }
    public double distanceToEnd() { return follower.distanceToEndpoint(); }
    public Path currentPath() { return follower.currentPath(); }
    public Curve currentCurve() { return follower.currentCurve(); }
    public int pathIndex() { return follower.pathIndex(); }
    public Pose closestPose() { return follower.closestPose(); }
    public Follower.Mode mode() { return follower.mode(); }
    public double velocityConstraint() { return constants.getVelocityConstraint(); }
    public boolean stationaryFinish() { return !follower.isBusy(); }
    public boolean inertialFinish() { return follower.atParametricEnd(); }

    public boolean lenientFinish() { return Math.abs(follower.tangentialVelocity()) < velocityConstraint() && distanceToEnd() < 4; }
    public void setManualDrive(double vertical, double horizontal, double pivot) {
        follower.manual(vertical, horizontal, pivot);
    }

    public void follow(Path path) { follower.follow(path); }

    public void hold(Pose pose) { follower.hold(pose); }

    public void interrupt() { follower.stop(); }

    public void initCore() {
        follower = constants.create(CoreOpMode.getInstance().hardwareMap);
        follower.setPose(startPose == null ? PoseStorage.lastPose : startPose);
        this.writeCore();
    }

    public void loopCore() {}

    public void writeCore() {
        Benchmark.of("follower", follower::update);
    }
    
    public void stopCore() {
        PoseStorage.lastPose = pose();
    }
}