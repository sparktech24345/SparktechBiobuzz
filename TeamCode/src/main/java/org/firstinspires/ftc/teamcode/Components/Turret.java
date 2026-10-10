package org.firstinspires.ftc.teamcode.Components;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import ro.sparktech24345.logicore.core.CoreModule;
import ro.sparktech24345.logicore.hardware.Controllers.VPIDController;
@Config
public class Turret {
    DcMotorEx rightMotor;
    DcMotorEx leftMotor;
    Gamepad gamepad;
    VPIDController controller;
    public static double target = 0;

    public Turret(HardwareMap map, Gamepad gp) {
        gamepad = gp;
        rightMotor = map.get(DcMotorEx.class, "frontleft");
        leftMotor = map.get(DcMotorEx.class, "frontright");
    }

    public void init() {
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        controller = new VPIDController(1.0, 1.0, 1.0, 1, 1);
    }
    public void loop() {
        double velocity = rightMotor.getVelocity();
        double power = controller.calculate(target, velocity);
        rightMotor.setPower(power);
        leftMotor.setPower(power);
    }
}