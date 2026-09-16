package ro.sparktech24345.logicore.core

import ro.sparktech24345.logicore.events.Event
import ro.sparktech24345.logicore.events.EventBus
import ro.sparktech24345.logicore.utils.MathUtils
import ro.sparktech24345.logicore.utils.PreciseTimer
import ro.sparktech24345.logicore.utils.PreciseTimer.TimeSpec

/**
 * Enhanced button input processing with state tracking and event emission.
 * Supports both digital buttons and analog axes with configurable detection logic.
 */
open class CoreButton(private var isPressed: () -> Double = { 0.0 }) {
    /** Event emitted when button is pressed */
    class ButtonPressEvent(val button: CoreButton) : Event()

    /** Event emitted when button is released */
    class ButtonReleaseEvent(val button: CoreButton) : Event()

    /** Event emitted when button toggle state changes */
    class ButtonToggleEvent(val button: CoreButton, val value: Boolean) : Event()

    /** Custom press detection logic - defaults to edge detection on button press */
    private var wasPressed: () -> Boolean = { MathUtils.eval(isPressed()) && !held }

    /** Custom release detection logic - defaults to edge detection on button release */
    private var wasReleased: () -> Boolean = { !MathUtils.eval(isPressed()) && held }

    /** Determines when toggle state changes: on press or on release */
    enum class ToggleMode {
        ON_PRESS,
        ON_RELEASE
    }

    var toggleMode = ToggleMode.ON_PRESS

    /** True on the frame when button transitions from not pressed to pressed */
    var pressed = false
        private set

    /** True on the frame when button transitions from pressed to not pressed */
    var released = false
        private set

    /** True while button is currently held down */
    var held = false
        private set

    /** Current toggle state based on toggleMode */
    var toggled = false
        private set

    /** Toggle state that changes on press (regardless of toggleMode) */
    var toggledOnPress = false
        private set

    /** Toggle state that changes on release (regardless of toggleMode) */
    var toggledOnRelease = false
        private set

    /** Duration of the most recent button hold */
    var heldTime = TimeSpec(0)
        private set
    private var heldTimer = PreciseTimer()

    companion object {
        /**
         * Create a button from a boolean input source.
         * @param isPressed Function that returns current button state
         * @param wasPressed Optional custom press detection logic
         * @param wasReleased Optional custom release detection logic
         */
        fun ofBool(
            isPressed: () -> Boolean,
            wasPressed: (() -> Boolean)? = null,
            wasReleased: (() -> Boolean)? = null
        ): CoreButton {
            val obj = CoreButton { MathUtils.eval(isPressed()) }
            if (wasPressed != null) obj.wasPressed = wasPressed
            if (wasReleased != null) obj.wasReleased = wasReleased
            return obj
        }

        /**
         * Create a button from a double input source (analog axis).
         * @param isPressed Function that returns current axis value
         * @param wasPressed Optional custom press detection logic
         * @param wasReleased Optional custom release detection logic
         */
        fun ofDouble(
            isPressed: () -> Double,
            wasPressed: (() -> Boolean)? = null,
            wasReleased: (() -> Boolean)? = null
        ): CoreButton {
            val obj = CoreButton(isPressed)
            if (wasPressed != null) obj.wasPressed = wasPressed
            if (wasReleased != null) obj.wasReleased = wasReleased
            return obj
        }
    }

    /** Get the raw input value without any processing */
    fun raw(): Double {
        return isPressed()
    }

    /**
     * Update button state and emit events.
     * Should be called every frame to maintain accurate state tracking.
     */
    fun update() {
        var input = MathUtils.eval(isPressed())
        pressed = wasPressed()
        if (pressed) EventBus.emit(ButtonPressEvent(this))
        released = wasReleased()
        if (released) EventBus.emit(ButtonReleaseEvent(this))
        held = input
        if (pressed) {
            heldTimer.start()
            toggledOnPress = !toggledOnPress
        }
        if (released) {
            heldTime = heldTimer.getTime()
            toggledOnRelease = !toggledOnRelease
        }
        val lastToggle = toggled
        toggled = when (toggleMode) {
            ToggleMode.ON_PRESS -> toggledOnPress
            ToggleMode.ON_RELEASE -> toggledOnRelease
        }
        if (toggled != lastToggle) EventBus.emit(ButtonToggleEvent(this, toggled))
    }
}