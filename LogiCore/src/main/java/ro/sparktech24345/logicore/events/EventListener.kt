package ro.sparktech24345.logicore.events

/**
 * Functional interface for event listeners.
 * Used with EventBus to handle events in a type-safe manner.
 *
 * @param T The event type this listener handles
 */
fun interface EventListener<T : Event> {
    /** Called when an event of type T is emitted */
    fun onEvent(event: T)
}