package ro.sparktech24345.logicore.core;

import com.qualcomm.robotcore.hardware.Gamepad;
import java.util.HashMap;
import java.util.Map;

import dev.anygeneric.blazeftc.BlazeFTC;
import ro.sparktech24345.logicore.utils.Benchmark;

/**
 * Enhanced gamepad input processing with button state tracking.
 * Supports two gamepads with comprehensive button/axis coverage and event emission.
 */
public class CoreGamepad implements CoreModule {
    /**
     * Enumeration of all supported buttons across both gamepads
     */

    private final Gamepad g1;
    private final Gamepad g2;

    public CoreGamepad(Gamepad g1, Gamepad g2) {
        this.g1 = g1;
        this.g2 = g2;
    }

    /** Access button state using array-like syntax: gamepad[Button.CROSS1] */
    public CoreButton get(Button button) { return buttons.get(button); }

    // Java: gamepad.get(Button.CROSS1)
    // Kotlin: gamepad[Button.CROSS1]

    public void loopCore() {}
    public void readCore() {
        Benchmark.of("gamepad", () -> {
            if (CoreOpMode.getInstance().getConfig().performanceEngine.get()
                    == PerformanceEngine.BLAZE)
                CoreOpMode.getInstance().updateGamepads();
            for (CoreButton button : buttons.values()) button.update();
        });
    }

    /** Complete button mapping for both gamepads with state tracking */
    private final Map<Button, CoreButton> buttons = new HashMap<>();

    public void initCore() {
        // =========================== GAMEPAD 1 =================================

        buttons.put(Button.CROSS1, CoreButton.ofBool(() -> g1.cross, g1::aWasPressed, g1::aWasReleased));
        buttons.put(Button.CIRCLE1, CoreButton.ofBool(() -> g1.circle, g1::bWasPressed, g1::bWasReleased));
        buttons.put(Button.SQUARE1, CoreButton.ofBool(() -> g1.square, g1::xWasPressed, g1::xWasReleased));
        buttons.put(Button.TRIANGLE1, CoreButton.ofBool(() -> g1.triangle, g1::yWasPressed, g1::yWasReleased));

        buttons.put(Button.DPAD_UP1, CoreButton.ofBool(() -> g1.dpad_up, g1::dpadUpWasPressed, g1::dpadUpWasReleased));
        buttons.put(Button.DPAD_DOWN1, CoreButton.ofBool(() -> g1.dpad_down, g1::dpadDownWasPressed, g1::dpadDownWasReleased));
        buttons.put(Button.DPAD_LEFT1, CoreButton.ofBool(() -> g1.dpad_left, g1::dpadLeftWasPressed, g1::dpadLeftWasReleased));
        buttons.put(Button.DPAD_RIGHT1, CoreButton.ofBool(() -> g1.dpad_right, g1::dpadRightWasPressed, g1::dpadRightWasReleased));

        buttons.put(Button.LEFT_STICK_X1, new CoreButton(() -> g1.left_stick_x));
        buttons.put(Button.LEFT_STICK_Y1, new CoreButton(() -> g1.left_stick_y));

        buttons.put(Button.RIGHT_STICK_X1, new CoreButton(() -> g1.right_stick_x));
        buttons.put(Button.RIGHT_STICK_Y1, new CoreButton(() -> g1.right_stick_y));

        buttons.put(Button.LEFT_TRIGGER1, CoreButton.ofBool(() -> g1.left_trigger_pressed, g1::leftTriggerWasPressed, g1::leftTriggerWasReleased));
        buttons.put(Button.RIGHT_TRIGGER1, CoreButton.ofBool(() -> g1.right_trigger_pressed, g1::rightTriggerWasPressed, g1::rightTriggerWasReleased));

        buttons.put(Button.LEFT_BUMPER1, CoreButton.ofBool(() -> g1.left_bumper, g1::leftBumperWasPressed, g1::leftBumperWasReleased));
        buttons.put(Button.RIGHT_BUMPER1, CoreButton.ofBool(() -> g1.right_bumper, g1::rightBumperWasPressed, g1::rightBumperWasReleased));

        buttons.put(Button.LEFT_STICK_BUTTON1, CoreButton.ofBool(() -> g1.left_stick_button, g1::leftStickButtonWasPressed, g1::leftStickButtonWasReleased));
        buttons.put(Button.RIGHT_STICK_BUTTON1, CoreButton.ofBool(() -> g1.right_stick_button, g1::rightStickButtonWasPressed, g1::rightStickButtonWasReleased));

        buttons.put(Button.OPTIONS1, CoreButton.ofBool(() -> g1.options, g1::optionsWasPressed, g1::optionsWasReleased));
        buttons.put(Button.SHARE1, CoreButton.ofBool(() -> g1.share, g1::shareWasPressed, g1::shareWasReleased));

// =========================== GAMEPAD 2 =================================

        buttons.put(Button.CROSS2, CoreButton.ofBool(() -> g2.cross, g2::aWasPressed, g2::aWasReleased));
        buttons.put(Button.CIRCLE2, CoreButton.ofBool(() -> g2.circle, g2::bWasPressed, g2::bWasReleased));
        buttons.put(Button.SQUARE2, CoreButton.ofBool(() -> g2.square, g2::xWasPressed, g2::xWasReleased));
        buttons.put(Button.TRIANGLE2, CoreButton.ofBool(() -> g2.triangle, g2::yWasPressed, g2::yWasReleased));

        buttons.put(Button.DPAD_UP2, CoreButton.ofBool(() -> g2.dpad_up, g2::dpadUpWasPressed, g2::dpadUpWasReleased));
        buttons.put(Button.DPAD_DOWN2, CoreButton.ofBool(() -> g2.dpad_down, g2::dpadDownWasPressed, g2::dpadDownWasReleased));
        buttons.put(Button.DPAD_LEFT2, CoreButton.ofBool(() -> g2.dpad_left, g2::dpadLeftWasPressed, g2::dpadLeftWasReleased));
        buttons.put(Button.DPAD_RIGHT2, CoreButton.ofBool(() -> g2.dpad_right, g2::dpadRightWasPressed, g2::dpadRightWasReleased));

        buttons.put(Button.LEFT_STICK_X2, new CoreButton(() -> g2.left_stick_x));
        buttons.put(Button.LEFT_STICK_Y2, new CoreButton(() -> g2.left_stick_y));

        buttons.put(Button.RIGHT_STICK_X2, new CoreButton(() -> g2.right_stick_x));
        buttons.put(Button.RIGHT_STICK_Y2, new CoreButton(() -> g2.right_stick_y));

        buttons.put(Button.LEFT_TRIGGER2, CoreButton.ofBool(() -> g2.left_trigger_pressed, g2::leftTriggerWasPressed, g2::leftTriggerWasReleased));
        buttons.put(Button.RIGHT_TRIGGER2, CoreButton.ofBool(() -> g2.right_trigger_pressed, g2::rightTriggerWasPressed, g2::rightTriggerWasReleased));

        buttons.put(Button.LEFT_BUMPER2, CoreButton.ofBool(() -> g2.left_bumper, g2::leftBumperWasPressed, g2::leftBumperWasReleased));
        buttons.put(Button.RIGHT_BUMPER2, CoreButton.ofBool(() -> g2.right_bumper, g2::rightBumperWasPressed, g2::rightBumperWasReleased));

        buttons.put(Button.LEFT_STICK_BUTTON2, CoreButton.ofBool(() -> g2.left_stick_button, g2::leftStickButtonWasPressed, g2::leftStickButtonWasReleased));
        buttons.put(Button.RIGHT_STICK_BUTTON2, CoreButton.ofBool(() -> g2.right_stick_button, g2::rightStickButtonWasPressed, g2::rightStickButtonWasReleased));

        buttons.put(Button.OPTIONS2, CoreButton.ofBool(() -> g2.options, g2::optionsWasPressed, g2::optionsWasReleased));
        buttons.put(Button.SHARE2, CoreButton.ofBool(() -> g2.share, g2::shareWasPressed, g2::shareWasReleased));
        buttons.forEach((button, coreButton) -> coreButton.setButton(button));
    }
}

