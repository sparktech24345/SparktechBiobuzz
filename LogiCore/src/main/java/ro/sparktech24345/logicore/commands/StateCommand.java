package ro.sparktech24345.logicore.commands;

import ro.sparktech24345.logicore.states.HasStates;
import ro.sparktech24345.logicore.states.StateSet;

/**
 * Command that sets a specific state on its owning module.
 * Uses the state ownership system to automatically find the target module.
 */
public class StateCommand<Dt, St extends StateSet<Dt>> extends BaseCommand {
    public StateCommand(HasStates<Dt, St> stateComponent, St state) {
        super(() -> stateComponent.state(state));
    }
}