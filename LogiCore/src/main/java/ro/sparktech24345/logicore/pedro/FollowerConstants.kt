package ro.sparktech24345.logicore.pedro

import com.pedropathing.follower.Follower
import com.qualcomm.robotcore.hardware.HardwareMap

interface FollowerConstants {
    fun create(map: HardwareMap): Follower
    fun getVelocityConstraint(): Double = 4.0
}