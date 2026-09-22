package ro.sparktech24345.logicore.utils

import ro.sparktech24345.logicore.core.CoreOpMode

object Benchmark {
    /**
     * Benchmark a block of code when debug mode is enabled.
     * Logs execution time to both telemetry and console.
     */
    fun of(
        name: String = "GENERIC_BENCHMARK_NAME",
        run: () -> Unit = {},
    ) {
        val bm = PreciseTimer(name).start()
        run()
        bm.log(CoreOpMode.instance!!.coreTelemetry)
        println("Timer: ${bm.name} -- ${bm.getTime().get()} ms")
    }
}