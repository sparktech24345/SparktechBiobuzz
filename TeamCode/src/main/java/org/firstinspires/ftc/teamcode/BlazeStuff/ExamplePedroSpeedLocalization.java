package org.firstinspires.ftc.teamcode.BlazeStuff;

import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Pedro.ConstantsBiobuzz;
import org.firstinspires.ftc.teamcode.Pedro.ConstantsDecode;

import dev.anygeneric.blazeftc.DummyPlugOpMode;
import dev.anygeneric.blazeftc_pedro.PedroSingleDataLocalizer;

@TeleOp(name = "Example Pedro High Speed Localization")
public class ExamplePedroSpeedLocalization extends DummyPlugOpMode {
    @Override
    public void runOpModeInBlaze() {
        initializeBlazeFTC();
        engageMotorAcceleration();
        //we create the pedro2 follower. NOTE that this uses the pinpoint java driver to set all your settings and offsets
        Follower follower = ConstantsDecode.createFollowerDecode(hardwareMap); // lets just pretend this exists for now
        waitForStart();
        ElapsedTime elt = new ElapsedTime();
        PedroSingleDataLocalizer.setup(follower, () -> {
            telemetry.addData("pedro loop time (ms)", elt.milliseconds());
            elt.reset();
            follower.update();
            telemetry.addData("x,y", follower.localizer.pose().x() + ", " + follower.localizer.pose().y());
        });
        //this is a test path. Replace it with your team's logic
//        follower.followPath(new Path(new BezierLine(new Pose(0, 0), new Pose(10, 0))));
        runBlazeFTC(0);

        //This should be replaced with your own code. 
        ElapsedTime elt2 = new ElapsedTime();
        while (!isStopRequested()) {
            for (LynxModule i : hardwareMap.getAll(LynxModule.class))
                i.clearBulkCache();
            //it doesn't matter what you do here
            sleep(20);
            telemetry.addData("main loop time (ms)", elt2.milliseconds());
            elt2.reset();
            telemetry.update();
        }
    }
}