package ro.sparktech24345.logicore.core;

import java.nio.file.AccessDeniedException;

import ro.sparktech24345.logicore.utils.WeightedArray;

/**
 * Manages a collection of modules with priority-based execution order.
 * Prevents module installation after the OpMode starts to ensure consistent state.
 */
public class ModuleHandler implements ModuleContainer {

    private WeightedArray<CoreModule> modules = new WeightedArray<>();

    /** Number of modules currently managed */
    public int getSize() { return modules.getSize(); }

    /** Prevents module installation after start() is called */
    private boolean lock = false;

    /**
     * Install a module with priority-based execution order.
     * Modules are initialized immediately upon installation.
     * 
     * @throws IllegalStateException if called after start() has been invoked
     */
    public <T extends CoreModule> T install(T module, double priority) {
        if (lock) throw new RuntimeException("Cannot install modules after start!");
        modules.add(module, priority);
        module.initCore();
        return module;
    }

    public void initCore() {}

    /** Update all modules during init_loop stage, in priority order */
    public void init_loopCore() {
        for (WeightedArray.Weighted<CoreModule> module : modules.list()) module.value.init_loopCore();
    }

    /** Lock the handler and start all modules */
    public void startCore() {
        lock = true;
        for (WeightedArray.Weighted<CoreModule> module : modules.list()) module.value.startCore();
    }

    /** Update all modules during main loop, in priority order */
    public void loopCore() {
        for (WeightedArray.Weighted<CoreModule> module : modules.list()) module.value.loopCore();
    }

    /** Stop all modules and clear the module list */
    public void stopCore() {
        for (WeightedArray.Weighted<CoreModule> module : modules.list()) module.value.stopCore();
        modules.clear();
    }

    public void readCore() {
        for (WeightedArray.Weighted<CoreModule> module : modules.list()) module.value.readCore();
    }

    public void writeCore() {
        for (WeightedArray.Weighted<CoreModule> module : modules.list()) module.value.writeCore();
    }
}
