package ro.sparktech24345.logicore.states;

public interface StateSet<T> {
    T value();

    default String stateName() {
        return "GENERIC_STATE_NAME";
    }
}
