package ro.sparktech24345.logicore.events;

/**
 * Base class for all events in the LogiCore event system.
 * Events can be cancelled to prevent further processing by listeners.
 */
public class Event {
    /**
     * Whether this event has been cancelled and should stop processing
     */
    private boolean cancelled = false;

    public boolean cancelled() {
        return this.cancelled;
    }

    public void cancelled(boolean val) {
        this.cancelled = val;
    }
}