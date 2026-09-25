package ro.sparktech24345.logicore.config

data class HardwareConfig(
    var id: Int,
    var port: Int,
) {
    constructor(id: Hubs, port: Int) : this(id.id, port)
}