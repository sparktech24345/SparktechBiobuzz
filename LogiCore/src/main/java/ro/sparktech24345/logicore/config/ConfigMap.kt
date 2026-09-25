package ro.sparktech24345.logicore.config

import java.util.concurrent.ConcurrentHashMap

object ConfigMap {
    private val map: ConcurrentHashMap<String, MotorConfig> = ConcurrentHashMap()

    operator fun set(name: String, value: MotorConfig) {
        map[name] = value
    }

    operator fun get(name: String) = map[name]
}