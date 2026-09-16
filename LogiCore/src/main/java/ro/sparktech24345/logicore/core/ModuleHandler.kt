package ro.sparktech24345.logicore.core

import ro.sparktech24345.logicore.utils.WeightedArray

/**
 * Manages a collection of modules with priority-based execution order.
 * Prevents module installation after the OpMode starts to ensure consistent state.
 */
class ModuleHandler : ModuleContainer {

    private var modules = WeightedArray<CoreModule>()

    /** Number of modules currently managed */
    val size: Int
        get() = modules.size

    /** Prevents module installation after start() is called */
    private var lock = false

    /**
     * Install a module with priority-based execution order.
     * Modules are initialized immediately upon installation.
     * 
     * @throws IllegalStateException if called after start() has been invoked
     */
    override fun <T : CoreModule> install(module: T, priority: Float): T {
        if (lock) error("A module was installed after init. Please install all modules before start!")
        modules.add(module, priority)
        module.init()
        return module
    }

    override fun init() = Unit

    /** Update all modules during init_loop stage, in priority order */
    override fun init_loop() {
        for (module in modules.list()) module.value.init_loop()
    }

    /** Lock the handler and start all modules */
    override fun start() {
        lock = true
        for (module in modules.list()) module.value.start()
    }

    /** Update all modules during main loop, in priority order */
    override fun loop() {
        for (module in modules.list()) module.value.loop()
    }

    /** Stop all modules and clear the module list */
    override fun stop() {
        for (module in modules.list()) module.value.stop()
        modules.clear()
    }
}
