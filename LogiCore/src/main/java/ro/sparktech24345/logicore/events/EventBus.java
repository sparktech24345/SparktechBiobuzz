package ro.sparktech24345.logicore.events;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global event bus for decoupled communication between components.
 * Implements publish-subscribe pattern for event-driven architecture.
 */
public class EventBus {
    private static final ConcurrentHashMap<Class<? extends Event>, List<EventListener<? extends Event>>> listeners = new ConcurrentHashMap<>();

    /**
     * Subscribe a listener to a specific event type.
     *
     * @param type     The event class to listen for
     * @param listener The listener to handle events
     */
    public static <T extends Event> void subscribe(Class<T> type, EventListener<T> listener) {
        if (listeners.containsKey(type)) Objects.requireNonNull(listeners.get(type)).add(listener);
        else {
            listeners.put(type, List.of(listener));
        }
    }

    /**
     * Unsubscribe a listener from a specific event type.
     *
     * @param type     The event class to stop listening for
     * @param listener The listener to remove
     */
    public static <T extends Event> void unsubscribe(Class<T> type, EventListener<T> listener) {
        List<EventListener<? extends Event>> list = Objects.requireNonNull(listeners.get(type));
        list.remove(listener);
        if (list.isEmpty()) listeners.remove(type, list);
    }

    /**
     * Emit an event to all subscribed listeners.
     * Listeners are called in subscription order until event is cancelled.
     *
     * @param event The event to emit
     */
    @SuppressWarnings("unchecked")
    public static <T extends Event> void emit(T event) {
        List<EventListener<? extends Event>> list = listeners.get(event.getClass());
        if (list == null) return;

        for (EventListener<? extends Event> listener : list) {
            ((EventListener<T>) listener).onEvent(event);

            if (event.cancelled()) break;
        }
    }

    /**
     * Check if there are active listeners for a specific event type.
     */
    public static boolean hasListeners(Class<? extends Event> type) {
        List<EventListener<? extends Event>> list = listeners.get(type);
        return list != null && !list.isEmpty();
    }

    /**
     * Clear all event listeners - useful for cleanup between OpModes
     */
    public static void cleanup() {
        listeners.clear();
    }
}