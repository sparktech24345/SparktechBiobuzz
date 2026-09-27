package ro.sparktech24345.logicore.commands;

import java.util.Objects;

import ro.sparktech24345.logicore.states.CoreState;

/**
 * Command that sets a specific state on its owning module.
 * Uses the state ownership system to automatically find the target module.
 */
public class StateCommand extends BaseCommand {
    public StateCommand(CoreState state) {
        super(() -> Objects.requireNonNull(state.getOwner()).setState(state));
    }
}