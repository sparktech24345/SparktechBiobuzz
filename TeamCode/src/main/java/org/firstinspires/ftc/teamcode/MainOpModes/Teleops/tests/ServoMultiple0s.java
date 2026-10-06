package org.firstinspires.ftc.teamcode.MainOpModes.Teleops.tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
@Config
@TeleOp(name = "ServoMultiple0", group = "Linear OpMode")
public class ServoMultiple0s extends LinearOpMode {
    public static double rightGatePos = 0.68; // 0.42 down 0.68 up
    public static double leftGatePos = 0.38; // 0.64 down 0.38 up
    public static double leftTiltPos = 0.9;
    public static double rightTiltPos = 0.9;
    public static double angleServoPos = 0.4; // max down is 0.9 and max up is 0.05
    public static double CameraServoPos = 0;
    public static double motorPow = 0;
    public static double coupleServoPos = 0;

    @Override
    public void runOpMode() {
        Servo leftTiltServo = hardwareMap.get(Servo.class, "leftTiltServo");
        Servo rightTiltServo = hardwareMap.get(Servo.class, "rightTiltServo");

        Servo rightGateServo = hardwareMap.get(Servo.class, "rightGateServo");
        Servo leftGateServo = hardwareMap.get(Servo.class, "leftGateServo");

        Servo turretAngleServo = hardwareMap.get(Servo.class, "turretAngleServo");

        Servo CameraRotateServo = hardwareMap.get(Servo.class, "CameraRotateServo");

        Servo coupleServo = hardwareMap.get(Servo.class, "coupleServo");


        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, "intakeMotor");


        while (opModeInInit()) {
            if (leftTiltServo != null) leftTiltServo.setPosition(leftTiltPos);// poses are syncronized, in bot = 1, out of bot = 0
            if (rightTiltServo != null) rightTiltServo.setPosition(rightTiltPos);

            if (rightGateServo != null) rightGateServo.setPosition(rightGatePos);
            if (leftGateServo != null) leftGateServo.setPosition(leftGatePos);

            if (CameraRotateServo != null) CameraRotateServo.setPosition(CameraServoPos);

            if (turretAngleServo != null) turretAngleServo.setPosition(angleServoPos); // max down is 0.88 and max up is 0.05
            if (coupleServo != null) coupleServo.setPosition(coupleServoPos);  // 0.16 coupled; 0.35 decoupled

        }

        while (opModeIsActive()) {
            if (leftTiltServo != null) leftTiltServo.setPosition(leftTiltPos);
            if (rightTiltServo != null) rightTiltServo.setPosition(rightTiltPos);

            if (rightGateServo != null) rightGateServo.setPosition(rightGatePos);
            if (leftGateServo != null) leftGateServo.setPosition(leftGatePos);

            if (CameraRotateServo != null) CameraRotateServo.setPosition(CameraServoPos);

            if (turretAngleServo != null) turretAngleServo.setPosition(angleServoPos);

            if (CameraRotateServo != null) CameraRotateServo.setPosition(CameraServoPos);

            if (coupleServo != null) coupleServo.setPosition(coupleServoPos);

            telemetry.addData("Pos: ", coupleServo.getPosition());
            telemetry.update();
            motor.setPower(motorPow);
        }
    }
}