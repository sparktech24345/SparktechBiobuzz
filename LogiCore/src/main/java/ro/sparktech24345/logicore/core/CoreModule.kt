package ro.sparktech24345.logicore.core

/**
 * Base interface for all modules in the LogiCore system.
 * Provides a standardized lifecycle that aligns with FTC OpMode stages.
 */
interface CoreModule {
    /** Called once during OpMode initialization - set up hardware and initial state */
    fun init()

    /** Called every loop cycle - update module logic */
    fun loop()

    /** Called during init_loop stage - optional initialization that repeats before start */
    fun init_loop() {}

    /** Called when OpMode transitions from init to running state */
    fun start() {}

    /** Called when OpMode stops - clean up resources */
    fun stop() {}

    /**
     * Central update method that routes to appropriate lifecycle method based on game stage.
     * This allows the module system to uniformly update all modules regardless of their current stage.
     */
    fun update(stage: CoreOpMode.GameStage) {
        when (stage) {
            CoreOpMode.GameStage.INIT -> init()
            CoreOpMode.GameStage.INIT_LOOP -> init_loop()
            CoreOpMode.GameStage.START -> start()
            CoreOpMode.GameStage.LOOP -> loop()
            CoreOpMode.GameStage.STOP -> stop()
        }
    }
}
