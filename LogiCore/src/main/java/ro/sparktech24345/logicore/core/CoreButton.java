package ro.sparktech24345.logicore.core;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import ro.sparktech24345.logicore.events.Event;
import ro.sparktech24345.logicore.events.EventBus;
import ro.sparktech24345.logicore.utils.MathUtils;
import ro.sparktech24345.logicore.utils.PreciseTimer;
import ro.sparktech24345.logicore.utils.TimeSpec;

/**
 * Enhanced button input processing with state tracking and event emission.
 * Supports both digital buttons and analog axes with configurable detection logic.
 */
class CoreButton {
    public CoreButton() {}
    public CoreButton(DoubleSupplier pressedSup) {
        this.pressedSup = pressedSup;
    }

    private DoubleSupplier pressedSup = () -> 0.0;

    /** Event emitted when button is pressed */
    static class ButtonPressEvent extends Event {
        private final CoreButton button;
        public CoreButton getButton() { return button; }
        public ButtonPressEvent(CoreButton button) {
            this.button = button;
        }
    }

    /** Event emitted when button is released */
    static class ButtonReleaseEvent extends Event {
        private final CoreButton button;
        public CoreButton getButton() { return button; }
        public ButtonReleaseEvent(CoreButton button) {
            this.button = button;
        }
    }

    /** Event emitted when button toggle state changes */
    static class ButtonToggleEvent extends Event {
        private final CoreButton button;
        public CoreButton getButton() { return button; }

        private final boolean toggled;
        public boolean getToggled() { return toggled; }
        public ButtonToggleEvent(CoreButton button, boolean toggleState) {
            this.button = button;
            this.toggled = toggleState;
        }
    }

    /** True while button is currently held down */
    private boolean held = false;
    public boolean isHeld() { return this.held; }

    /** Custom press detection logic - defaults to edge detection on button press */

    private BooleanSupplier wasPressed = () -> MathUtils.eval(pressedSup.getAsDouble()) && !held;

    /** Custom release detection logic - defaults to edge detection on button release */
    private BooleanSupplier wasReleased = () -> !MathUtils.eval(pressedSup.getAsDouble()) && held;

    /** Determines when toggle state changes: on press or on release */
    enum ToggleMode {
        ON_PRESS,
        ON_RELEASE
    }

    private ToggleMode toggleMode = ToggleMode.ON_PRESS;
    public void setToggleMode(ToggleMode mode) { this.toggleMode = mode; }

    /** True on the frame when button transitions from not pressed to pressed */
    private boolean pressed = false;
    public boolean isPressed() { return this.pressed; }

    /** True on the frame when button transitions from pressed to not pressed */
    private boolean released = false;
    public boolean isReleased() { return this.released; }

    /** Current toggle state based on toggleMode */
    private boolean toggled = false;
    public boolean isToggled() { return this.toggled; }

    /** Toggle state that changes on press (regardless of toggleMode) */
    private boolean toggledOnPress = false;

    /** Toggle state that changes on release (regardless of toggleMode) */
    private boolean toggledOnRelease = false;

    /** Duration of the most recent button hold */
    private TimeSpec holdTime = new TimeSpec(0);
    public TimeSpec getHoldTime() { return this.holdTime; }
    private final PreciseTimer heldTimer = new PreciseTimer();
    public static CoreButton ofBool(BooleanSupplier isPressed, BooleanSupplier wasPressed, BooleanSupplier wasReleased) {
        CoreButton obj = new CoreButton(() -> MathUtils.eval(isPressed.getAsBoolean()));
        if (wasPressed != null) obj.wasPressed = wasPressed;
        if (wasReleased != null) obj.wasReleased = wasReleased;
        return obj;
    }
    public static CoreButton ofBool(BooleanSupplier isPressed) { return ofBool(isPressed, null, null); }
    public static CoreButton ofDouble(DoubleSupplier isPressed, BooleanSupplier wasPressed, BooleanSupplier wasReleased) {
        CoreButton obj = new CoreButton(isPressed);
        if (wasPressed != null) obj.wasPressed = wasPressed;
        if (wasReleased != null) obj.wasReleased = wasReleased;
        return obj;
    }

    /** Get the raw input value without any processing */
     double raw() {
        return pressedSup.getAsDouble();
    }

    /**
     * Update button state and emit events.
     * Should be called every frame to maintain accurate state tracking.
     */
    void update() {
        boolean input = MathUtils.eval(pressedSup.getAsDouble());
        pressed = wasPressed.getAsBoolean();
        if (pressed) EventBus.INSTANCE.emit(new ButtonPressEvent(this));
        released = wasReleased.getAsBoolean();
        if (released) EventBus.INSTANCE.emit(new ButtonReleaseEvent(this));
        held = input;
        if (pressed) {
            heldTimer.start();
            toggledOnPress = !toggledOnPress;
        }
        if (released) {
            this.holdTime = heldTimer.getTime();
            toggledOnRelease = !toggledOnRelease;
        }
        boolean lastToggle = toggled;
        switch (toggleMode) {
            case ON_PRESS: toggled = toggledOnPress;
            case ON_RELEASE: toggled = toggledOnRelease;
        }
        if (toggled != lastToggle) EventBus.INSTANCE.emit(new ButtonToggleEvent(this, toggled));
    }
}