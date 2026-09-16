package ro.sparktech24345.logicore.commands

import ro.sparktech24345.logicore.states.CoreState

/**
 * Command that sets a specific state on its owning module.
 * Uses the state ownership system to automatically find the target module.
 *
 * @param state The state to set on the owning module
 */
class StateCommand(state: CoreState) : BaseCommand({ state.owner!!.setState(state) })