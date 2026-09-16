package ro.sparktech24345.logicore.states

/**
 * Base class for state collections with ownership tracking.
 * Provides automatic state registration and module ownership assignment.
 */
open class BaseStateSet {
    /** Track all states created through make() for ownership assignment */
    var stateArray: MutableList<CoreState> = mutableListOf()

    /** Default zero state for convenience */
    val ZERO = make(CoreState(0.0, "ZERO"))

    /** Default state used when no specific state is requested */
    open val DEFAULT = ZERO

    /**
     * Create and register a state in this set.
     * The state is tracked for later ownership assignment.
     *
     * @param state The state to register
     * @return The same state for chaining
     */
    fun <T : CoreState> make(state: T): T {
        stateArray += state
        return state
    }

    /**
     * Assign ownership of all registered states to a module.
     * Called during module initialization to establish state->module relationships.
     *
     * @param module The module that will own all states in this set
     */
    fun <T : HasStates<out BaseStateSet>> own(module: T) {
        for (state in stateArray) state.owner = module
    }
}
