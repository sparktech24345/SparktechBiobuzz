package ro.sparktech24345.logicore.states

/**
 * Interface for objects that can manage states.
 * Provides access to a state set and state setting functionality.
 *
 * @param T The type of state set this object manages
 */
interface HasStates<T : BaseStateSet> {
    /** The state set containing all available states for this object */
    val states: T

    /**
     * Set the object to a specific state.
     * The state must be compatible with this object's state set.
     *
     * @param state The state to apply
     */
    fun <S : CoreState> setState(state: S)
}
