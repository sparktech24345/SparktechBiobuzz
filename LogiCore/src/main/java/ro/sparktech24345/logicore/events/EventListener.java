package ro.sparktech24345.logicore.events;

/**
 * Functional interface for event listeners.
 * Used with EventBus to handle events in a type-safe manner.
 */
@FunctionalInterface
public interface EventListener<T extends Event> {
    /** Called when an event of type T is emitted */
    void onEvent(T event);
}