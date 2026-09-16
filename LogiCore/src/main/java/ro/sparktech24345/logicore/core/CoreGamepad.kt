package ro.sparktech24345.logicore.core

import com.qualcomm.robotcore.hardware.Gamepad

/**
 * Enhanced gamepad input processing with button state tracking.
 * Supports two gamepads with comprehensive button/axis coverage and event emission.
 */
class CoreGamepad(
    val g1: Gamepad,
    val g2: Gamepad
) : CoreModule {
    /** Enumeration of all supported buttons across both gamepads */
    enum class Button {
        CROSS1, CIRCLE1, SQUARE1, TRIANGLE1,
        DPAD_UP1, DPAD_DOWN1, DPAD_RIGHT1, DPAD_LEFT1,
        LEFT_STICK_X1, LEFT_STICK_Y1,
        RIGHT_STICK_X1, RIGHT_STICK_Y1,
        LEFT_TRIGGER1, RIGHT_TRIGGER1,
        LEFT_BUMPER1, RIGHT_BUMPER1,
        LEFT_STICK_BUTTON1, RIGHT_STICK_BUTTON1,
        OPTIONS1, SHARE1,

        CROSS2, CIRCLE2, SQUARE2, TRIANGLE2,
        DPAD_UP2, DPAD_DOWN2, DPAD_RIGHT2, DPAD_LEFT2,
        LEFT_STICK_X2, LEFT_STICK_Y2,
        RIGHT_STICK_X2, RIGHT_STICK_Y2,
        LEFT_TRIGGER2, RIGHT_TRIGGER2,
        LEFT_BUMPER2, RIGHT_BUMPER2,
        LEFT_STICK_BUTTON2, RIGHT_STICK_BUTTON2,
        OPTIONS2, SHARE2
    }

    /** Access button state using array-like syntax: gamepad[Button.CROSS1] */
    operator fun get(button: Button): CoreButton = buttons[button]!!

    override fun init() = Unit
    override fun init_loop() = loop()
    override fun loop() = buttons.values.forEach { it.update() }

    /** Complete button mapping for both gamepads with state tracking */
    val buttons = mapOf<Button, CoreButton>(

        // =========================== GAMEPAD 1 =================================

        Button.CROSS1 to CoreButton.ofBool({ g1.cross }, g1::aWasPressed, g1::aWasReleased),
        Button.CIRCLE1 to CoreButton.ofBool({ g1.circle }, g1::bWasPressed, g1::bWasReleased),
        Button.SQUARE1 to CoreButton.ofBool({ g1.square }, g1::xWasPressed, g1::xWasReleased),
        Button.TRIANGLE1 to CoreButton.ofBool({ g1.triangle }, g1::yWasPressed, g1::yWasReleased),

        Button.DPAD_UP1 to CoreButton.ofBool(
            { g1.dpad_up },
            g1::dpadUpWasPressed,
            g1::dpadUpWasReleased
        ),
        Button.DPAD_DOWN1 to CoreButton.ofBool(
            { g1.dpad_down },
            g1::dpadDownWasPressed,
            g1::dpadDownWasReleased
        ),
        Button.DPAD_LEFT1 to CoreButton.ofBool(
            { g1.dpad_left },
            g1::dpadLeftWasPressed,
            g1::dpadLeftWasReleased
        ),
        Button.DPAD_RIGHT1 to CoreButton.ofBool(
            { g1.dpad_right },
            g1::dpadRightWasPressed,
            g1::dpadRightWasReleased
        ),

        Button.LEFT_STICK_X1 to CoreButton { g1.left_stick_x.toDouble() },
        Button.LEFT_STICK_Y1 to CoreButton { g1.left_stick_y.toDouble() },

        Button.RIGHT_STICK_X1 to CoreButton.ofDouble({ g1.right_stick_x.toDouble() }),
        Button.RIGHT_STICK_Y1 to CoreButton.ofDouble({ g1.right_stick_y.toDouble() }),

        Button.LEFT_TRIGGER1 to CoreButton.ofBool(
            { g1.left_trigger_pressed },
            g1::leftTriggerWasPressed,
            g1::leftTriggerWasReleased
        ),
        Button.RIGHT_TRIGGER1 to CoreButton.ofBool(
            { g1.right_trigger_pressed },
            g1::rightTriggerWasPressed,
            g1::rightTriggerWasReleased
        ),

        Button.LEFT_BUMPER1 to CoreButton.ofBool(
            { g1.left_bumper },
            g1::leftBumperWasPressed,
            g1::leftBumperWasReleased
        ),
        Button.RIGHT_BUMPER1 to CoreButton.ofBool(
            { g1.right_bumper },
            g1::rightBumperWasPressed,
            g1::rightBumperWasReleased
        ),

        Button.LEFT_STICK_BUTTON1 to CoreButton.ofBool(
            { g1.left_stick_button },
            g1::leftStickButtonWasPressed,
            g1::leftStickButtonWasReleased
        ),
        Button.RIGHT_STICK_BUTTON1 to CoreButton.ofBool(
            { g1.right_stick_button },
            g1::rightStickButtonWasPressed,
            g1::rightStickButtonWasReleased
        ),

        Button.OPTIONS1 to CoreButton.ofBool(
            { g1.options },
            g1::optionsWasPressed,
            g1::optionsWasReleased
        ),
        Button.SHARE1 to CoreButton.ofBool(
            { g1.share },
            g1::shareWasPressed,
            g1::shareWasReleased
        ),

        // =========================== GAMEPAD 2 =================================

        Button.CROSS2 to CoreButton.ofBool({ g2.cross }, g2::aWasPressed, g2::aWasReleased),
        Button.CIRCLE2 to CoreButton.ofBool({ g2.circle }, g2::bWasPressed, g2::bWasReleased),
        Button.SQUARE2 to CoreButton.ofBool({ g2.square }, g2::xWasPressed, g2::xWasReleased),
        Button.TRIANGLE2 to CoreButton.ofBool({ g2.triangle }, g2::yWasPressed, g2::yWasReleased),

        Button.DPAD_UP2 to CoreButton.ofBool(
            { g2.dpad_up },
            g2::dpadUpWasPressed,
            g2::dpadUpWasReleased
        ),
        Button.DPAD_DOWN2 to CoreButton.ofBool(
            { g2.dpad_down },
            g2::dpadDownWasPressed,
            g2::dpadDownWasReleased
        ),
        Button.DPAD_LEFT2 to CoreButton.ofBool(
            { g2.dpad_left },
            g2::dpadLeftWasPressed,
            g2::dpadLeftWasReleased
        ),
        Button.DPAD_RIGHT2 to CoreButton.ofBool(
            { g2.dpad_right },
            g2::dpadRightWasPressed,
            g2::dpadRightWasReleased
        ),

        Button.LEFT_STICK_X2 to CoreButton { g2.left_stick_x.toDouble() },
        Button.LEFT_STICK_Y2 to CoreButton { g2.left_stick_y.toDouble() },

        Button.RIGHT_STICK_X2 to CoreButton.ofDouble({ g2.right_stick_x.toDouble() }),
        Button.RIGHT_STICK_Y2 to CoreButton.ofDouble({ g2.right_stick_y.toDouble() }),

        Button.LEFT_TRIGGER2 to CoreButton.ofBool(
            { g2.left_trigger_pressed },
            g2::leftTriggerWasPressed,
            g2::leftTriggerWasReleased
        ),
        Button.RIGHT_TRIGGER2 to CoreButton.ofBool(
            { g2.right_trigger_pressed },
            g2::rightTriggerWasPressed,
            g2::rightTriggerWasReleased
        ),

        Button.LEFT_BUMPER2 to CoreButton.ofBool(
            { g2.left_bumper },
            g2::leftBumperWasPressed,
            g2::leftBumperWasReleased
        ),
        Button.RIGHT_BUMPER2 to CoreButton.ofBool(
            { g2.right_bumper },
            g2::rightBumperWasPressed,
            g2::rightBumperWasReleased
        ),

        Button.LEFT_STICK_BUTTON2 to CoreButton.ofBool(
            { g2.left_stick_button },
            g2::leftStickButtonWasPressed,
            g2::leftStickButtonWasReleased
        ),
        Button.RIGHT_STICK_BUTTON2 to CoreButton.ofBool(
            { g2.right_stick_button },
            g2::rightStickButtonWasPressed,
            g2::rightStickButtonWasReleased
        ),

        Button.OPTIONS2 to CoreButton.ofBool(
            { g2.options },
            g2::optionsWasPressed,
            g2::optionsWasReleased
        ),
        Button.SHARE2 to CoreButton.ofBool(
            { g2.share },
            g2::shareWasPressed,
            g2::shareWasReleased
        )
    )
}
