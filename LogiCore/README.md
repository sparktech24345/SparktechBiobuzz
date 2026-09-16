# LogiCore
[![](https://www.jitpack.io/v/andrei-147/LogiCore.svg)](https://www.jitpack.io/#andrei-147/LogiCore)

Kotlin/Java library for FTC robotics programming that provides clean architecture and abstractions for robot control.
**Made by team Sparktech #24345**

## Features

- **Modular Architecture**: Priority-based module system with lifecycle management
- **Command Pattern**: Sequential and parallel command execution
- **Hardware Abstractions**: Motor, servo, sensor, and gamepad control
- **State Management**: IDE-friendly state system with autocomplete
- **Event System**: Decoupled communication between components
- **Throttled Updates**: Configurable update intervals for expensive operations

## Installation

To install LogiCore, add the following lines to your FTC project's `build.dependencies.gradle` file.

```gradle
repositories {
    mavenCentral()
    google() // Basic FTC repository links

    // (1) Add these 2 lines
    maven { url 'https://jitpack.io' }
    maven { url 'https://repo.dairy.foundation/releases' }
}

dependencies {
    // (2) Add this line also
    implementation 'com.github.andrei-147:LogiCore:1.0.0'


    implementation 'org.firstinspires.ftc:Inspection:12.0.0' // Basic FTC dependencies
    implementation 'org.firstinspires.ftc:Blocks:12.0.0'
    implementation 'org.firstinspires.ftc:RobotCore:12.0.0'
    implementation 'org.firstinspires.ftc:RobotServer:12.0.0'
    implementation 'org.firstinspires.ftc:OnBotJava:12.0.0'
    implementation 'org.firstinspires.ftc:Hardware:12.0.0'
    implementation 'org.firstinspires.ftc:FtcCommon:12.0.0'
    implementation 'org.firstinspires.ftc:Vision:12.0.0'
    implementation 'androidx.appcompat:appcompat:1.2.0'
}
```

After adding these changes, sync your Gradle project.

## Setup

Add the library to your project and extend `CoreOpMode` instead of the standard FTC `OpMode`:

```kotlin
class MyTeleOp : CoreOpMode(OpModeType.TELEOP) {
    override fun onInit() {
        // Install your modules
        val motor = install(CoreMotor("leftMotor", MyMotorStates()))
    }
    
    override fun onLoop() {
        // Your main loop logic
    }
}
```

## Basic Usage

### Motor Control
```kotlin
val motorStates = DriveMotorStateSet()
val leftMotor = install(CoreMotor("leftMotor", motorStates))

// Use states for cleaner code
leftMotor.setState(motorStates.FULL_POWER)
```

### Command System
```kotlin
// Queue commands for sequential execution
queue(DelayCommand(TimeSpec.fromSeconds(1.0)))
queue(StateCommand(motorStates.HALF_POWER))

// Execute commands in parallel
execute(motorCommand)
execute(servoCommand)
```

### Gamepad Input
```kotlin
// Access button states with event emission
if (gamepad[Button.CROSS1].pressed) {
    // Button was just pressed
}

if (gamepad[Button.CROSS1].held) {
    // Button is currently held down
}
```

## Architecture

- **Core Module System**: Standardized lifecycle (init, loop, start, stop)
- **Hardware Layer**: Abstractions for motors, servos, sensors
- **Command Layer**: Sequential and parallel command execution
- **State Layer**: Type-safe state management with ownership
- **Event Layer**: Publish-subscribe pattern for decoupled communication

## License

See LICENSE file for details.
