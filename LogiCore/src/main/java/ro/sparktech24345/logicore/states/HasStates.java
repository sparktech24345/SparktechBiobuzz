package ro.sparktech24345.logicore.states;

public interface HasStates<Dt, St extends StateSet<Dt>> {
    Class<St> states();

    St state();

    void state(St state);
}
