package ro.sparktech24345.logicore.core

/**
 * Interface for objects that can contain and manage other modules.
 * Allows hierarchical module organization (e.g., subsystems containing motors).
 */
interface ModuleContainer : CoreModule {
    /**
     * Install a module into this container with specified priority.
     * Higher priority modules are updated first during each cycle.
     *
     * @param module The module to install
     * @param priority Execution priority (higher values = earlier execution)
     * @return The installed module for chaining
     */
    fun <T : CoreModule> install(module: T, priority: Float = 1.0f): T
}