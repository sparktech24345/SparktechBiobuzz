package ro.sparktech24345.logicore.states;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for state collections with ownership tracking.
 * Provides automatic state registration and module ownership assignment.
 */
public class BaseStateSet<T> {
    /** Track all states created through make() for ownership assignment */
    private final List<CoreState<T>> stateArray = new ArrayList<>();

    /** Default state used when no specific state is requested */
    public CoreState<T> DEFAULT;

    /**
     * Create and register a state in this set.
     * The state is tracked for later ownership assignment.
     *
     * @param state The state to register
     * @return The same state for chaining
     */
    public <Ty extends CoreState<T>> Ty register(Ty state) {
        stateArray.add(state);
        return state;
    }

    /**
     * Assign ownership of all registered states to a module.
     * Called during module initialization to establish state->module relationships.
     *
     * @param module The module that will own all states in this set
     */
    public <Ty extends HasStates<T, ? extends BaseStateSet<T>>> void own(Ty module) {
        for (CoreState<T> state : stateArray) state.setOwner(module);
    }

    public final CoreState<T> state(T data, String name) {
        return register(new CoreState<>(data, name));
    }
}
