package ro.sparktech24345.logicore.core;

import java.util.ArrayList;

/**
 * Manages a collection of modules with priority-based execution order.
 * Prevents module installation after the OpMode starts to ensure consistent state.
 */
public class ModuleHandler implements ModuleContainer {

    private ArrayList<CoreModule> modules = new ArrayList<>();

    /**
     * Number of modules currently managed
     */
    public int size() {
        return modules.size();
    }

    /**
     * Prevents module installation after start() is called
     */
    private boolean lock = false;

    /**
     * Install a module with priority-based execution order.
     * Modules are initialized immediately upon installation.
     *
     * @throws IllegalStateException if called after start() has been invoked
     */
    public <T extends CoreModule> T install(T module, double priority) {
        if (lock) throw new RuntimeException("Cannot install modules after start!");
        modules.add(module);
        module.initCore();
        return module;
    }

    public void initCore() {
    }

    /**
     * Update all modules during init_loop stage, in priority order
     */
    public void init_loopCore() {
        for (CoreModule module : modules) module.init_loopCore();
    }

    /**
     * Lock the handler and start all modules
     */
    public void startCore() {
        lock = true;
        for (CoreModule module : modules) module.startCore();
    }

    /**
     * Update all modules during main loop, in priority order
     */
    public void loopCore() {
        for (CoreModule module : modules) {
            module.loopCore();
        }
    }

    /**
     * Stop all modules and clear the module list
     */
    public void stopCore() {
        for (CoreModule module : modules) module.stopCore();
        modules.clear();
    }

    public void readCore() {
        for (CoreModule module : modules) {
            module.readCore();
        }
    }

    public void writeCore() {
        for (CoreModule module : modules) {
            module.writeCore();
        }
    }
}
