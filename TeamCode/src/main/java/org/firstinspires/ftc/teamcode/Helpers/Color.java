package org.firstinspires.ftc.teamcode.Helpers;

import static org.firstinspires.ftc.teamcode.Helpers.GlobalStorage.ballColorTresholdBlue;
import static org.firstinspires.ftc.teamcode.Helpers.GlobalStorage.ballColorTresholdGreen;
import static org.firstinspires.ftc.teamcode.Helpers.GlobalStorage.leftSensorColorMultiplier;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;


public enum Color {
    GREEN,
    PURPLE,
    NONE;

    public static Color getColor(double r, double g, double b) {
        if (b > 0.001 && g > 0.001) { //first check if there is a ball then compare to determine what it is
            if (b * 1.2 > g) return PURPLE;
            else return GREEN;
        } else return NONE;

    }

    public static Color getColorForTurret(NormalizedRGBA colors) {
        return getColorForTurret(colors.red * 10000.0, colors.green * 10000.0, colors.blue * 10000.0);
    }

    public static Color getColorForTurret(double r, double g, double b) {
        if (g < 9.5) return NONE;
        else if (b > g) return PURPLE;
        else return GREEN;
    }


    public static Color getColorForStorage(NormalizedRGBA colors) {
        return getColorForStorage(colors.red * 10000.0, colors.green * 10000.0, colors.blue * 10000.0);
    }

    public static Color getColorForStorage(NormalizedRGBA colors, boolean isLeft) {
        if (isLeft)
            return getColorForStorage(colors.red * 10000.0 * leftSensorColorMultiplier, colors.green * 10000.0 * leftSensorColorMultiplier, colors.blue * 10000.0 * leftSensorColorMultiplier);
        else
            return getColorForStorage(colors.red * 10000.0, colors.green * 10000.0, colors.blue * 10000.0);
    }

    public static Color getColorForStorage(double r, double g, double b) {
        if (g < ballColorTresholdGreen && b < ballColorTresholdBlue) return NONE;
        else if (b > g) return PURPLE;
        else return GREEN;
    }

    public static Color getCameraColor(LLResult llResult) {
        if (llResult != null) {
            double[] pythonOutputs = llResult.getPythonOutput();
            if (pythonOutputs != null) {
                if (pythonOutputs[2] <= pythonOutputs[6]) {
                    return PURPLE;
                } else return GREEN;
            } else return NONE;
        } else return NONE;
    }


}
