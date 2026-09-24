package ro.sparktech24345.logicore.pedro

import com.pedropathing.api.PoseFactory
import com.pedropathing.drivetrain.DrivePowers
import com.pedropathing.follower.Follower
import com.pedropathing.math.Pose
import ro.sparktech24345.logicore.core.CoreModule
import com.pedropathing.paths.Path
import com.pedropathing.paths.curves.Curve
import ro.sparktech24345.logicore.core.CoreOpMode
import ro.sparktech24345.logicore.utils.Benchmark
import kotlin.math.abs

class CoreFollower<NeededConstants: FollowerConstants>(
    private val constants: NeededConstants,
    val startPose: Pose = Pose.zero(),
    val poseFactory: PoseFactory = PoseFactory.degrees(),
    private val initWithLastPose: Boolean = false
): CoreModule {
    var follower: Follower? = null

    var pose: Pose
        get() = follower!!.pose()
        set(value) = follower!!.setPose(value)

    val progress: Double
        get() = follower!!.completion()

    val distanceLeft: Double
        get() = follower!!.remainingDistance()

    val distanceToEnd: Double
        get() = follower!!.distanceToEndpoint()

    val currentPath: Path
        get() = follower!!.currentPath()

    val currentCurve: Curve
        get() = follower!!.currentCurve()

    val pathIndex: Int
        get() = follower!!.pathIndex()

    val closestPose: Pose
        get() = follower!!.closestPose()

    val mode: Follower.Mode
        get() = follower!!.mode()

    val velocityConstraint: Double
        get() = constants.getVelocityConstraint()

    val stationaryFinish: Boolean
        get() = !follower!!.isBusy

    val inertialFinish: Boolean
        get() = follower!!.atParametricEnd()

    val lenientFinish: Boolean
        get() = abs(follower!!.tangentialVelocity()) < velocityConstraint && distanceToEnd < 4

    var manualPower = DrivePowers(0.0, 0.0, 0.0)
        set(value) {
            field = value
            follower!!.manual(value)
        }

    fun follow(path: Path) = follower!!.follow(path)

    fun hold(pose: Pose) = follower!!.hold(pose)

    fun interrupt() = follower!!.stop()

    override fun initCore() {
        follower = constants.create(CoreOpMode.instance!!.hardwareMap)
        follower!!.setPose(if (initWithLastPose) PoseStorage.lastPose else startPose)
        Benchmark.of("follower") { follower!!.update() }
    }

    override fun loopCore() = Unit

    override fun writeCore() = Benchmark.of("follower") {
        follower!!.update()
    }
    
    override fun stopCore() {
        PoseStorage.lastPose = pose
    }
    
    class PoseStorage {
        companion object {
            var lastPose: Pose = Pose.zero()
        }
    }
}