package ro.sparktech24345.logicore.states

/**
 * Base state class for hardware state management.
 * States own their value and have a reference to their owning module for command execution.
 *
 * @param value The numeric value associated with this state
 * @param name Descriptive name for debugging and telemetry
 */
open class CoreState(val value: Double, val name: String = "GENERIC_STATE_NAME") {
    /** The module that owns this state (set during module initialization) */
    var owner: HasStates<out BaseStateSet>? = null
}