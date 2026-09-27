package org.firstinspires.ftc.teamcode.MainOpModes.Teleops;

import org.firstinspires.ftc.teamcode.Components.Configs;

import ro.sparktech24345.logicore.core.Button;
import ro.sparktech24345.logicore.core.CoreButton;
import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.events.EventBus;

public class testclass extends CoreOpMode {

    public testclass() {
        super(Configs.teleopCfg);
    }

    @Override
    public void onInit() {
        EventBus.subscribe(CoreButton.ButtonPressEvent.class, (event) -> {
            CoreButton button = event.getButton();
            Button type = button.getButton();
            if (type == Button.CROSS1) {
                System.out.println("Button " + Button.CROSS1 + " was pressed!");
            }
        });
    }

    @Override
    public void onLoop() {

    }
}
