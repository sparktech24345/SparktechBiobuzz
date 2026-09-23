package org.firstinspires.ftc.teamcode.MainOpModes.Teleops

import ro.sparktech24345.logicore.commands.BaseCommand
import ro.sparktech24345.logicore.core.CoreOpMode


class test: CoreOpMode(MainTeleOP.cfg) {

    val value = 0.0

    var lambda: () -> Unit = {}
    var lambda2: (Int, Int) -> Int = { _, _ -> print("Execut lambda"); 0 }

    override fun onInit() {
        lambda2 = { v1, v2 ->
            print("Execut lambda"); print("test")
            v1 * 3 + v2 * 6
        }
    }

    override fun onLoop() {

    }
}