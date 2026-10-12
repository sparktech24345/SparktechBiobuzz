package ro.sparktech24345.logicore.core;

/**
 * Who owns the drivetrain motors. Exactly one side writes powers at a time.
 * Set the starting owner with {@code OpModeConfig.driveMode}, switch at runtime with
 * {@code CoreOpMode.driveMode(DriveMode)}.
 */
public enum DriveMode {
    /** The gamepad {@code DriveTrain} module drives (normal teleop). */
    GAMEPAD,
    /** Pedro drives through {@code SparkMecanum} (autonomous, or path-following in teleop). */
    FOLLOWER
}
