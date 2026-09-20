package org.firstinspires.ftc.teamcode.BlazeStuff;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Pedro.ConstantsBiobuzz;

import dev.anygeneric.blazeftc.BlazeDummyPlug;
import dev.anygeneric.blazeftc.BlazeFTC;
import dev.anygeneric.blazeftc.Hub;
import dev.anygeneric.blazeftc_pedro.PedroSingleDataLocalizer;

public class BlazeOpMode extends OpMode {
    boolean motorInPlace = false;
    int target = 500;
    Follower follower = null;
    Path pathToFollow;
    @Override
    public void init() {
        BlazeDummyPlug.initializeBlazeFTC(hardwareMap);
        BlazeDummyPlug.engageMotorAccel(hardwareMap);
        follower = ConstantsBiobuzz.create(hardwareMap);
        DcMotor motor = hardwareMap.get(DcMotor.class, "motor");
        //do whatever else init stuff you need to here
        ElapsedTime elt = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        PedroSingleDataLocalizer.setup(follower, () -> {
            telemetry.addData("pedro loop time (ms)", elt.milliseconds());
            elt.reset();
            telemetry.addData(
                    "x,y",
                    follower.pose().x() + ", " + follower.pose().y()
            );
            if (follower.currentPath() != pathToFollow) {
                follower.follow(pathToFollow);
            }
        });

        ElapsedTime elt2 = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        BlazeDummyPlug.engageBulkReadAcceleration(hardwareMap, Hub.ExHub, 1, () -> {
            //Every time this function is called, you should have new encoder data available in your motors. Run PID loops here.
            //I recommend doing it like this, report your data do not do computation here:
            if (motor.getCurrentPosition() == target) {
                motorInPlace = true;
            }
            telemetry.addData("bulk loop time (ms)", elt2.milliseconds());
            elt2.reset();
            return null;
        });
    }

    @Override
    public void start() {
        BlazeFTC.run(0);
    }

    @Override
    public void loop() {
        telemetry.update();
        //do what you like here. Personally I'd use a command library. but as a trivial example:
        if (motorInPlace) {
            pathToFollow = line(new Pose(0.0, 0.0), new Pose(10.0, 10.0));
            if (follower.currentPath() != null && follower.atParametricEnd()) {
                telemetry.addData("done", true);
            }
        }
    }

    @Override
    public void stop() {
        BlazeDummyPlug.closeBlazeFTC();
    }
}