package ro.sparktech24345.logicore.states;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for state collections with ownership tracking.
 * Provides automatic state registration and module ownership assignment.
 */
public class BaseStateSet {
    /** Track all states created through make() for ownership assignment */
    private final List<CoreState> stateArray = new ArrayList<>();

    /** Default zero state for convenience */
    public final CoreState ZERO = register(new CoreState(0.0, "ZERO"));

    /** Default state used when no specific state is requested */
    public CoreState DEFAULT = ZERO;

    /**
     * Create and register a state in this set.
     * The state is tracked for later ownership assignment.
     *
     * @param state The state to register
     * @return The same state for chaining
     */
    public <T extends CoreState> T register(T state) {
        stateArray.add(state);
        return state;
    }

    /**
     * Assign ownership of all registered states to a module.
     * Called during module initialization to establish state->module relationships.
     *
     * @param module The module that will own all states in this set
     */
    public <T extends HasStates<? extends BaseStateSet>> void own(T module) {
        for (CoreState state : stateArray) state.setOwner(module);
    }
}
