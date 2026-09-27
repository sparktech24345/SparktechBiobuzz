package ro.sparktech24345.logicore.states;

/**
 * Base state class for hardware state management.
 * States own their value and have a reference to their owning module for command execution.
 */
public class CoreState {
    public CoreState(double value, String name) {
        this.value = value;
        this.name = name;
    }
    private final double value;
    public double getValue() { return this.value; }
    private final String name;
    public String getName() { return this.name; }
    /** The module that owns this state (set during module initialization) */
    private HasStates<? extends BaseStateSet> owner = null;
    public void setOwner(HasStates<? extends BaseStateSet> owner) { this.owner = owner; }
    public HasStates<? extends BaseStateSet> getOwner() { return this.owner; }
}