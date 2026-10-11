package org.firstinspires.ftc.teamcode.MainOpModes.Teleops.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Components.Turret;

@TeleOp(name = "turret test", group = "Testing")
public class TurretTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Turret turret = new Turret(hardwareMap,gamepad1);
        turret.init();

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            turret.loop();
        }

    }
}
