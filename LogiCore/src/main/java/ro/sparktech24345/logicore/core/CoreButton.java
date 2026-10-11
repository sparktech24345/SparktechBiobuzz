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
public class CoreButton {
    public CoreButton() {
    }

    public CoreButton(DoubleSupplier pressedSup) {
        this.pressedSup = pressedSup;
    }

    private Button actualButton = Button.NONE;

    public Button button() {
        return this.actualButton;
    }

    public void button(Button button) {
        this.actualButton = button;
    }

    private DoubleSupplier pressedSup = () -> 0.0;

    /**
     * Event emitted when button is pressed
     */
    public static class ButtonPressEvent extends Event {
        private final CoreButton button;

        public CoreButton button() {
            return button;
        }

        public ButtonPressEvent(CoreButton button) {
            this.button = button;
        }
    }

    /**
     * Event emitted when button is released
     */
    public static class ButtonReleaseEvent extends Event {
        private final CoreButton button;

        public CoreButton button() {
            return button;
        }

        public ButtonReleaseEvent(CoreButton button) {
            this.button = button;
        }
    }

    /**
     * Event emitted when button toggle state changes
     */
    public static class ButtonToggleEvent extends Event {
        private final CoreButton button;

        public CoreButton button() {
            return button;
        }

        private final boolean toggled;

        public boolean toggled() {
            return toggled;
        }

        public ButtonToggleEvent(CoreButton button, boolean toggleState) {
            this.button = button;
            this.toggled = toggleState;
        }
    }

    private static int globalFrameCount = 0;
    public static void advanceFrame() {
        globalFrameCount++;
    }
    private int lastUpdatedFrame = -1;

    public void ensureUpdated() {
        if (lastUpdatedFrame == globalFrameCount) return;
        lastUpdatedFrame = globalFrameCount;
        updateInternal();
    }

    /**
     * True while button is currently held down
     */
    private boolean held = false;

    public boolean held() {
        ensureUpdated();
        return this.held;
    }

    /**
     * Custom press detection logic - defaults to edge detection on button press
     */

    private BooleanSupplier wasPressed = () -> MathUtils.eval(pressedSup.getAsDouble()) && !held;

    /**
     * Custom release detection logic - defaults to edge detection on button release
     */
    private BooleanSupplier wasReleased = () -> !MathUtils.eval(pressedSup.getAsDouble()) && held;

    /**
     * Determines when toggle state changes: on press or on release
     */
    enum ToggleMode {
        ON_PRESS,
        ON_RELEASE
    }

    private ToggleMode toggleMode = ToggleMode.ON_PRESS;

    public void toggleMode(ToggleMode mode) {
        this.toggleMode = mode;
    }

    /**
     * True on the frame when button transitions from not pressed to pressed
     */
    private boolean pressed = false;

    public boolean pressed() {
        ensureUpdated();
        return this.pressed;
    }

    /**
     * True on the frame when button transitions from pressed to not pressed
     */
    private boolean released = false;

    public boolean released() {
        ensureUpdated();
        return this.released;
    }

    /**
     * Current toggle state based on toggleMode
     */
    private boolean toggled = false;

    public boolean toggled() {
        ensureUpdated();
        return this.toggled;
    }

    /**
     * Toggle state that changes on press (regardless of toggleMode)
     */
    private boolean toggledOnPress = false;

    /**
     * Toggle state that changes on release (regardless of toggleMode)
     */
    private boolean toggledOnRelease = false;

    /**
     * Duration of the most recent button hold
     */
    private TimeSpec holdTime = new TimeSpec(0);

    public TimeSpec holdTime() {
        ensureUpdated();
        return this.holdTime;
    }

    private final PreciseTimer heldTimer = new PreciseTimer();

    public static CoreButton ofBool(BooleanSupplier isPressed, BooleanSupplier wasPressed, BooleanSupplier wasReleased) {
        CoreButton obj = new CoreButton(() -> MathUtils.eval(isPressed.getAsBoolean()));
        if (wasPressed != null) obj.wasPressed = wasPressed;
        if (wasReleased != null) obj.wasReleased = wasReleased;
        return obj;
    }

    public static CoreButton ofBool(BooleanSupplier isPressed) {
        return ofBool(isPressed, null, null);
    }

    public static CoreButton ofDouble(DoubleSupplier isPressed, BooleanSupplier wasPressed, BooleanSupplier wasReleased) {
        CoreButton obj = new CoreButton(isPressed);
        if (wasPressed != null) obj.wasPressed = wasPressed;
        if (wasReleased != null) obj.wasReleased = wasReleased;
        return obj;
    }

    /**
     * Get the raw input value without any processing
     */
    double raw() {
        ensureUpdated();
        return pressedSup.getAsDouble();
    }

    /**
     * Update button state and emit events.
     * Should be called every frame to maintain accurate state tracking.
     */
    void update() {
        ensureUpdated();
    }

    void updateInternal() {
        boolean input = MathUtils.eval(pressedSup.getAsDouble());
        pressed = wasPressed.getAsBoolean();
        released = wasReleased.getAsBoolean();
        held = input;

        boolean hasPress = EventBus.hasListeners(ButtonPressEvent.class);
        boolean hasRelease = EventBus.hasListeners(ButtonReleaseEvent.class);
        boolean hasToggle = EventBus.hasListeners(ButtonToggleEvent.class);

        if (pressed) {
            if (hasPress) EventBus.emit(new ButtonPressEvent(this));
            heldTimer.start();
            toggledOnPress = !toggledOnPress;
        }
        if (released) {
            if (hasRelease) EventBus.emit(new ButtonReleaseEvent(this));
            this.holdTime = heldTimer.time();
            toggledOnRelease = !toggledOnRelease;
        }
        boolean lastToggle = toggled;
        switch (toggleMode) {
            case ON_PRESS:
                toggled = toggledOnPress;
                break;
            case ON_RELEASE:
                toggled = toggledOnRelease;
                break;
        }
        if (toggled != lastToggle && hasToggle) {
            EventBus.emit(new ButtonToggleEvent(this, toggled));
        }
    }
}
