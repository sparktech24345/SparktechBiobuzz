package ro.sparktech24345.logicore.states;

/**
 * Interface for objects that can manage states.
 * Provides access to a state set and state setting functionality.
 */
public interface HasStates<Dt, St extends BaseStateSet<Dt>> {
    St getStates();

    CoreState<Dt> currentState();

    /**
     * Set the object to a specific state.
     * The state must be compatible with this object's state set.
     *
     * @param state The state to apply
     */
    void setState(CoreState<Dt> state);
}
