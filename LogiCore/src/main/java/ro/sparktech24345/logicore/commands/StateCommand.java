package ro.sparktech24345.logicore.commands;

import java.util.Objects;

import ro.sparktech24345.logicore.states.CoreState;
import ro.sparktech24345.logicore.states.HasStates;

/**
 * Command that sets a specific state on its owning module.
 * Uses the state ownership system to automatically find the target module.
 */
public class StateCommand<Dt> extends BaseCommand {
    public StateCommand(CoreState<Dt> state) {
        this(state.getOwner(), state);
    }
    public StateCommand(HasStates<Dt, ?> stateComponent, CoreState<Dt> state) {
        super(() -> stateComponent.setState(state));
    }
}