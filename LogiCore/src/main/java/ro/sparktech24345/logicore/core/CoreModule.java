package ro.sparktech24345.logicore.core;

/**
 * Base interface for all modules in the LogiCore system.
 * Provides a standardized lifecycle that aligns with FTC OpMode stages.
 */
public interface CoreModule {
    /** All of them have been rewritten to have "do" in the beginning so they don't clash with DummyOpMode
     * that has Linear op mode as such solution handicap prevails*/

    /**
     * Called once during OpMode initialization - set up hardware and initial state
     */
    void initCore();

    /**
     * Called every loop cycle - update module logic
     */
    void loopCore();

    /**
     * Called during init_loop stage - optional initialization that repeats before start
     */
    public default void init_loopCore() {
    }

    /**
     * Called when OpMode transitions from init to running state
     */
    default void startCore() {
    }

    /**
     * Called when OpMode stops - clean up resources
     */
    default void stopCore() {
    }

    default void readCore() {
    }

    default void writeCore() {
    }

    /**
     * Central update method that routes to appropriate lifecycle method based on game stage.
     * This allows the module system to uniformly update all modules regardless of their current stage.
     */
    default void doUpdate(GameStage stage) {
        switch (stage) {
            case INIT:
                initCore();
                break;
            case INIT_LOOP:
                init_loopCore();
                break;
            case START:
                startCore();
                break;
            case LOOP:
                loopCore();
                break;
            case STOP:
                stopCore();
                break;
        }
    }
}
