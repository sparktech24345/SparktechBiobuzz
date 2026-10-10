package org.firstinspires.ftc.teamcode.Helpers;

import com.acmerobotics.dashboard.config.Config;

@Config
public class GlobalStorage {


    /// =============================== MOTORS ===============================
    public static String frontLeftMotorName = "frontleft";
    public static String backLeftMotorName = "backleft";
    public static String backRightMotorName = "backright";
    public static String frontRightMotorName = "frontright";
    public static String rightIntakeMotorName = "rightintakemotor";
    public static String leftIntakeMotorName = "leftintakemotor";
    public static String rightOuttakeMotorName = "rightouttakemotor";
    public static String leftOuttakeMotorName = "leftouttakemotor";


    /// =============================== Servos ===============================
    public static String leftTurretRingServo = "leftturretringservo";
    public static String rightTurretRingServo = "rightturretringservo";
    public static String angleServoName = "angleservo";
    public static String ballsBlockingServo = "ballsblockingservo";
    public static String flowerCollectingServo = "flowercollectingservo";
    public static String coupleServo = "coupleServo";


    // ===================== OLD DECODE NAMES ==========================
    public static String intakeMotorName = "intakeMotor"; // port control 3
    public static String turretRotationMotorName = "turretRotateMotor"; // port control 2
    public static String turretFlyWheelMotorLeftName = "turretFlyWheelMotorLeft"; // port control 0
    public static String turretFlyWheelMotorRightName = "turretFlyWheelMotorRight"; // port control 1

    public static String rightGateServoName = "rightGateServo"; // port 2
    public static String leftGateServoName = "leftGateServo"; // port 3
    public static String rightTiltServoName = "rightTiltServo"; // port
    public static String leftTiltServoName = "leftTiltServo"; // port
    public static String turretAngleServoName = "turretAngleServo"; // port 4
    public static String CameraRotateServoName = "CameraRotateServo"; // port 0
    public static String PTOServoName = "PTOServo"; // port 1
    public static String colorSensorRightName = "colorSensorRight";
    public static String colorSensorLeftName = "colorSensorLeft";
    public static String distanceSensorName = "distanceSensor";


    /// =============================== Sensors ===============================

    public static String pinpointName = "pinpoint";
    public static String octoQuadName = "octoquad";
    public static String limelightName = "limelight";
    public static String colorSensor1Name = "colorsensor1";
    public static String colorSensor2Name = "colorsensor2";
    public static double ballColorTresholdBlue = 6;
    public static double ballColorTresholdGreen = 6;
    public static double leftSensorColorMultiplier = 0.6;
    public static double camId = 0;

}
