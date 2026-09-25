package ro.sparktech24345.logicore.config

import com.pedropathing.config.ConfigVar
import com.pedropathing.config.Configuration

data class MotorConfig(
    val id: Int,
    val port: Int,
) {
    constructor(id: Hubs, port: Int) : this(id.id, port)
}