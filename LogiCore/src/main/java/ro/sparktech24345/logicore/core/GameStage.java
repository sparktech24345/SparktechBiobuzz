package ro.sparktech24345.logicore.core;

/**
 * Represents the different stages of an OpMode lifecycle
 */
public enum GameStage {
    INIT,       // Initial setup phase
    INIT_LOOP,  // Repeating initialization before start
    START,      // Transition from init to running
    LOOP,       // Main running loop
    STOP,       // Cleanup phase
}
