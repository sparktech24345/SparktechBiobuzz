package org.firstinspires.ftc.teamcode

import com.pedropathing.algorithm.Foresight
import com.pedropathing.algorithm.ForesightConfig
import com.pedropathing.controllers.Controller
import com.pedropathing.follower.Follower
import com.pedropathing.math.Matrix
import com.pedropathing.math.Vector2D
import com.pedropathing.revhub.drivetrains.Mecanum
import com.pedropathing.revhub.drivetrains.MecanumConfig
import com.pedropathing.revhub.localizers.PinpointConfig
import com.pedropathing.revhub.localizers.PinpointLocalizer
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import ro.sparktech24345.logicore.pedro.FollowerConstants

class MyConstants: FollowerConstants {

    val pinpointConfig = PinpointConfig { c ->
        c.name.set("pinpoint")
        c.globalDistanceUnit.set(DistanceUnit.INCH)
        c.offsetUnits.set(DistanceUnit.INCH)
        c.xPodOffset.set(-4.4055) // was forwardPodY, -0.1119 m converted to inches
        c.yPodOffset.set(-7.2696) // was strafePodX, -0.18467 m converted to inches
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD) // was forwardEncoderDirection
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD) // was strafeEncoderDirection

    }

    companion object {
        const val FRONT_RIGHT_NAME: String = "frontright"
        const val FRONT_LEFT_NAME: String = "frontleft"
        const val BACK_RIGHT_NAME: String = "backright"
        const val BACK_LEFT_NAME: String = "backleft"
    }

    val mecanumConfig = MecanumConfig { c ->
        c.frontLeftName.set(FRONT_LEFT_NAME)
        c.frontRightName.set(FRONT_RIGHT_NAME)
        c.backLeftName.set(BACK_LEFT_NAME)
        c.backRightName.set(BACK_RIGHT_NAME)

        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD)
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD)
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE)
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE)

    }

    val foresightConfig = ForesightConfig { c ->
        val primaryTranslationalForward = Controller.proportional(0.5584405897733354)
        val secondaryTranslationalForward = Controller.proportional(0.20632887611386982)
        val primaryTranslationalLateral = Controller.proportional(1.126224128077316)
        val secondaryTranslationalLateral = Controller.proportional(0.41610972206163055)

        c.forwardTranslational.set(
            Controller.piecewise(secondaryTranslationalForward)
                .put(2.5, primaryTranslationalForward)
        )
        c.strafeTranslational.set(
            Controller.piecewise(secondaryTranslationalLateral)
                .put(2.5, primaryTranslationalLateral)
        )

        c.coast.set(Controller.proportionalFeedforward(0.013098337555642468))
        c.brake.set(Controller.proportionalFeedforward(0.011133586922296098))

        c.headingFeedback.set(Controller.proportional(8.671465667145917))
        c.headingBrakeCoefficients.set(
            Vector2D.cartesian(
                0.07339220630227516,
                0.006859957538411244
            )
        )

        c.linearBrakeCoefficients.set(Matrix.diag(0.15899263914570266, 0.048539631719911935))
        c.quadraticBrakeCoefficients.set(Matrix.diag(0.0013378711158710745, 0.0025696484933031205))

        c.maxAchievableForwardVelocity.set(39.36239120567458)
        c.maxAchievableStrafeVelocity.set(32.235486134630044)
        c.naturalForwardDeceleration.set(32.68057926229764)
        c.naturalStrafeDeceleration.set(63.332597032903756)
    }

    override fun create(map: HardwareMap): Follower {
        return Follower(PinpointLocalizer(map, pinpointConfig), Mecanum(map, mecanumConfig), Foresight(foresightConfig))
    }

    override fun getVelocityConstraint(): Double = foresightConfig.velocityConstraint.get()
}