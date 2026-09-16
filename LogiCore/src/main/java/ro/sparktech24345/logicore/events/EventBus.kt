package ro.sparktech24345.logicore.events

/**
 * Global event bus for decoupled communication between components.
 * Implements publish-subscribe pattern for event-driven architecture.
 */
object EventBus {
    private val listeners = mutableMapOf<Class<out Event>, MutableList<EventListener<out Event>>>()

    /**
     * Subscribe a listener to a specific event type.
     *
     * @param type The event class to listen for
     * @param listener The listener to handle events
     */
    fun <T : Event> subscribe(
        type: Class<T>,
        listener: EventListener<T>
    ) {
        listeners
            .getOrPut(type) { mutableListOf() }
            .add(listener)
    }

    /**
     * Unsubscribe a listener from a specific event type.
     *
     * @param type The event class to stop listening for
     * @param listener The listener to remove
     */
    fun <T : Event> unsubscribe(
        type: Class<T>,
        listener: EventListener<T>
    ) {
        listeners[type]?.let { list ->
            list.remove(listener)
            if (list.isEmpty()) {
                listeners.remove(type)
            }
        }
    }

    /**
     * Emit an event to all subscribed listeners.
     * Listeners are called in subscription order until event is cancelled.
     *
     * @param event The event to emit
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Event> emit(event: T) {
        val list = listeners[event::class.java] ?: return

        for (listener in list) {
            (listener as EventListener<T>).onEvent(event)

            if (event.cancelled) break
        }
    }

    /** Clear all event listeners - useful for cleanup between OpModes */
    fun cleanup() = listeners.clear()
}