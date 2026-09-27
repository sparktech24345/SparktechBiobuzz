package ro.sparktech24345.logicore.core;

/**
 * Interface for objects that can contain and manage other modules.
 * Allows hierarchical module organization (e.g., subsystems containing motors).
 */
public interface ModuleContainer extends CoreModule {
    /**
     * Install a module into this container with specified priority.
     * Higher priority modules are updated first during each cycle.
     */
    <T extends CoreModule> T install(T module, double priority);
}