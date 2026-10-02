package ro.sparktech24345.logicore.states;

/**
 * Base state class for hardware state management.
 * States own their value and have a reference to their owning module for command execution.
 */
public class CoreState<T> {
    public CoreState(T value, String name) {
        this.value = value;
        this.name = name;
    }
    private final T value;
    public T getValue() { return this.value; }
    private final String name;
    public String getName() { return this.name; }
    /** The module that owns this state (set during module initialization) */
    private HasStates<T, ? extends BaseStateSet<T>> owner = null;
    public void setOwner(HasStates<T, ? extends BaseStateSet<T>> owner) { this.owner = owner; }
    public HasStates<T, ? extends BaseStateSet<T>> getOwner() { return this.owner; }
}