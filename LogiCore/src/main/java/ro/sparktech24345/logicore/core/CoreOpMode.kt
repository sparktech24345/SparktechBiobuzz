package ro.sparktech24345.logicore.core

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.seattlesolvers.solverslib.photon.PhotonCore
import ro.sparktech24345.logicore.commands.BaseCommand
import ro.sparktech24345.logicore.hardware.CoreVoltageSensor
import ro.sparktech24345.logicore.utils.PreciseTimer

/**
 * Base class for all LogiCore OpModes. Provides unified lifecycle management,
 * module system, command queuing, and hardware access.
 *
 * @param type The type of OpMode (TELEOP, AUTONOMOUS, or TESTING)
 * @param dash Whether to enable FTC Dashboard telemetry integration
 * @param debug Enable debug mode for benchmarking and additional logging
 */
@Suppress("PROPERTY_HIDES_JAVA_FIELD")
abstract class CoreOpMode(
    val type: OpModeType,
    val dash: Boolean = true,
    var debug: Boolean = false,
    val enablePhoton: Boolean = true,
) : OpMode(), CommandQueuer, ModuleContainer {
    init {
        instance = this
    }

    /** Represents the different stages of an OpMode lifecycle */
    enum class GameStage {
        INIT,       // Initial setup phase
        INIT_LOOP,  // Repeating initialization before start
        START,      // Transition from init to running
        LOOP,       // Main running loop
        STOP,       // Cleanup phase
    }

    private var modules = ModuleHandler()
    private var queuer = CoreQueuer()

    companion object {
        /** Global instance accessor for hardware components that need OpMode context */
        var instance: CoreOpMode? = null
            private set
    }

    /** Current stage of the OpMode lifecycle */
    var stage = GameStage.INIT
        private set

    enum class OpModeType {
        TELEOP,
        AUTONOMOUS,
        TESTING
    }

    /** Telemetry system with update throttling and multi-output support */
    val telemetry = CoreTelemetry()

    /** Gamepad input processing with button state tracking */
    lateinit var gamepad: CoreGamepad

    /** Voltage monitoring for battery health tracking */
    val voltageSensor = CoreVoltageSensor()

    /** Control Hub and Expansion Hub handlers for bulk reads */
    val hubs = CoreHubs()

    /** Install a module into the system with priority-based execution order */
    final override fun <T : CoreModule> install(module: T, priority: Float): T =
        modules.install(module, priority)

    /** Execute a command immediately (bypasses queue) */
    final override fun execute(command: BaseCommand) = queuer.execute(command)

    /** Queue a command for sequential execution */
    final override fun queue(command: BaseCommand) = queuer.queue(command)

    /** Clear all pending and executing commands */
    final override fun clear() = queuer.clear()

    /**
     * Benchmark a block of code when debug mode is enabled.
     * Logs execution time to both telemetry and console.
     */
    private fun benchmark(
        run: () -> Unit = {},
        name: String = "GENERIC_BENCHMARK_NAME"
    ) {
        if (!debug) return
        val bm = PreciseTimer(name).start()
        run()
        bm.log(telemetry)
        println("Timer: ${bm.name} -- ${bm.getTime().get() ?: 0} ms")
    }

    /**
     * Central update function that coordinates all system updates in the correct order:
     * 1. Clear hardware bulk caches
     * 2. Update input systems (gamepad, sensors)
     * 3. Execute user code for current stage
     * 4. Update user modules
     * 5. Process command queue
     * 6. Update telemetry (last, to capture all changes)
     */
    private fun upd(fn: () -> Unit) {
        hubs.update(stage)
        gamepad.update(stage)
        voltageSensor.update(stage)
        fn()
        modules.update(stage)
        queuer.update(stage)
        telemetry.update(stage)
    }

    final override fun init() {
        instance = this
        // ============ PHOTON SETTINGS ============

        if (enablePhoton) {
            PhotonCore.experimental.setMaximumParallelCommands(8)
            PhotonCore.PARALLELIZE_SERVOS = false
            PhotonCore.enable()
        }


        // ====== GAMEPAD + TELEMETRY SETUP =======
        gamepad = CoreGamepad(gamepad1, gamepad2)
        telemetry.addTelemetry(super.telemetry)

        // ============================ EXECUTING THE USER WRITTEN CODE ============================
        upd(this::onInit)
        stage = GameStage.INIT_LOOP
    }

    final override fun init_loop() {
        upd(this::onInitLoop)
    }

    final override fun start() {
        stage = GameStage.START
        upd(this::onStart)
        stage = GameStage.LOOP
    }

    final override fun loop() {
        upd(this::onLoop)
    }

    final override fun stop() {
        stage = GameStage.STOP
        upd(this::onStop)
        instance = null
    }

    /** User-defined initialization logic - called once during init stage */
    abstract fun onInit()

    /** Optional user logic for init_loop stage - called repeatedly before start */
    open fun onInitLoop() {}

    /** Optional user logic for start stage - called when transitioning to running */
    open fun onStart() {}

    /** User-defined main loop logic - called every cycle during running */
    abstract fun onLoop()

    /** Optional user cleanup logic - called when OpMode stops */
    open fun onStop() {}
}
