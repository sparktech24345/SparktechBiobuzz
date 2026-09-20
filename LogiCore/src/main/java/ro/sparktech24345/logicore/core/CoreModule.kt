package ro.sparktech24345.logicore.core

/**
 * Base interface for all modules in the LogiCore system.
 * Provides a standardized lifecycle that aligns with FTC OpMode stages.
 */
interface CoreModule {
    /** All of them have been rewritten to have "do" in the beginning so they don't clash with DummyOpMode
     * that has Linear op mode as such solution handicap prevails*/

    /** Called once during OpMode initialization - set up hardware and initial state */
    fun initCore()

    /** Called every loop cycle - update module logic */
    fun loopCore()

    /** Called during init_loop stage - optional initialization that repeats before start */
    fun init_loopCore() {}

    /** Called when OpMode transitions from init to running state */
    fun startCore() {}

    /** Called when OpMode stops - clean up resources */
    fun stopCore() {}

    /**
     * Central update method that routes to appropriate lifecycle method based on game stage.
     * This allows the module system to uniformly update all modules regardless of their current stage.
     */
    fun doUpdate(stage: CoreOpMode.GameStage) {
        when (stage) {
            CoreOpMode.GameStage.INIT -> initCore()
            CoreOpMode.GameStage.INIT_LOOP -> init_loopCore()
            CoreOpMode.GameStage.START -> startCore()
            CoreOpMode.GameStage.LOOP -> loopCore()
            CoreOpMode.GameStage.STOP -> stopCore()
        }
    }
}
