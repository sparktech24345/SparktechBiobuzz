# LogiCore-pedropathing

An official compatibility and translation layer that integrates the **PedroPathing** advanced path follower seamlessly into the **LogiCore** ecosystem.
<br>**Made by team Sparktech #24345**

## Features

- **Lifecycle Integration**: Wraps PedroPathing's follower inside a native LogiCore `CoreModule`, handling hardware initialization and background loops automatically.
- **Command-Based Pathing**: Out-of-the-box `BaseCommand` implementations (`FollowCommand`, `HoldCommand`) for scheduling paths and holds inside your sequential or parallel queues.
- **Automated Coordinate Continuity**: Built-in `PoseStorage` layer that automatically caches and persists the robot's coordinate positioning from the end of Autonomous directly into your TeleOp routines.
- **Multi-Level Finish Conditions**: Native property wrappers for `stationaryFinish`, `inertialFinish`, and `lenientFinish` configurations to determine exactly when a movement command completes.

## Installation

To add the PedroPathing compatibility layer to your project, include the custom PedroPathing Maven server and the JitPack dependency within your FTC project's `build.dependencies.gradle` file:

```kotlin
repositories {
    mavenCentral()
    google()

    // Add these repositories if not already present
    maven { url 'https://jitpack.io' }
    maven { url 'https://repo.dairy.foundation/releases' }
}

dependencies {
    // Add the LogiCore core library and the PedroPathing wrapper layer
    implementation 'com.github.andrei-147:LogiCore:1.0.0'
    implementation 'com.github.andrei-147:LogiCore-pedropathing:1.0.0'
    // Also add the PedroPathing dependencies
    implementation 'com.pedropathing:revhub:3.0.0'
    implementation 'com.pedropathing:tuning:1.0.0'
}
```

---

## Setup & Configuration

### 1. Implement FollowerConstants
Create a configuration file implementing `FollowerConstants` to cleanly pass your custom tuning settings and drivetrain generation logic to the module:

```kotlin
class MyRobotConstants : FollowerConstants {
    override fun create(map: HardwareMap): Follower {
        // Return your tuned PedroPathing Follower instance here
        return Follower(map) 
    }

    override fun getVelocityConstraint(): Double = 4.0 // Cut-off constraint for lenient finishes
                                                       // 4.0 is the PedroPathing value recomendation for this variable
                                                       // source: https://pedropathing.com/docs/pathing/guide/follow-state
}
```

### 2. Install the CoreFollower Module
Install the `CoreFollower` module into your `CoreOpMode` just like any other hardware abstraction element:

```kotlin
class MyAutonomous : CoreOpMode(OpModeType.AUTONOMOUS) {
    private lateinit var follower: CoreFollower<MyRobotConstants>

    override fun onInit() {
        val constants = MyRobotConstants()
        // Install the pathing wrapper module
        follower = install(CoreFollower(constants, startPose = Pose(10.0, 10.0, 90.0)))
    }
}
```

---

## Basic Usage

### Running Commands Sequentially
Queue path-following and point-holding routines seamlessly inside your command schedules without manually calling updates or looping checks:

```kotlin
override fun onStart() {
    val scorePose = follower.poseFactory.of(10.0, 20.0, 0.0)
    val parkPose = follower.poseFactory.of(10.0, 0.0, 180.0)

    // Queue movement blocks sequentially
    queue(
        FollowCommand(
            follower, Paths.line(follower.pose, scorePose).linear(follower.pose, scorePose)
        ).apply{ finishCondition = { follower.lenientFinish } }
    )
    queue(DelayCommand(TimeSpec.fromSeconds(0.5))) // Wait for manipulator mechanisms
    queue(HoldCommand(follower, scorePose, TimeSpec.fromSeconds(2.0), { follower.stationaryFinish })) // Secure positional hold
    queue(FollowCommand(
            follower, Paths.line(follower.pose, parkPose).linear(follower.pose, parkPose), { follower.lenientFinish }
        )) // Go to parkPose and stop following when lenientFinish is true
}
```

### Auto-to-TeleOp Pose Continuity
When launching a TeleOp routine immediately following an Autonomous match, configure `initWithLastPose = true` to seamlessly transfer positioning metrics over without resetting the localizer:

```kotlin
class MyTeleOp : CoreOpMode(OpModeType.TELEOP) {
    override fun onInit() {
        // Initializes perfectly wherever the autonomous routine finished moving
        val follower = install(CoreFollower(MyRobotConstants(), initWithLastPose = true))
    }
}
```

---

## Architecture Overview

- **CoreFollower Layer**: A lifecycle module (`CoreModule`) coordinating localized telemetry and coordinate-updating loops in the background.
- **Command Abstraction Layer**: Converts state endpoints (`follower.isBusy`, completions) directly into execution constraints (`isFinished`) expected by LogiCore's active `BaseCommand` layout.

## License

See LICENSE file for details.
