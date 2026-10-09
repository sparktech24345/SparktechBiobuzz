package ro.sparktech24345.logicore.utils;

/**
 * Mathematical utility functions for robotics calculations.
 * Provides common operations used in robot control and sensor processing.
 */
public class MathUtils {
    /**
     * Calculate absolute value of a number
     */
    static public double abs(double num) {
        return num > 0 ? num : -num;
    }

    /**
     * Get the sign of a number (-1.0, 0.0, or 1.0)
     */
    static public double signum(double num) {
        if (num < 0.0) return -1.0;
        if (num > 0.0) return 1.0;
        return 0.0;
    }

    /**
     * Find the maximum value among multiple numbers
     */
    static public double max(double... nums) {
        if (nums.length == 0) return 0.0;
        double mx = nums[0];
        for (double num : nums) mx = mx < num ? num : mx;
        return mx;
    }

    /**
     * Find the minimum value among multiple numbers
     */
    static public double min(double... nums) {
        if (nums.length == 0) return 0.0;
        double mx = nums[0];
        for (double num : nums) mx = mx > num ? num : mx;
        return mx;
    }

    /**
     * Clamp a number to be within a specified range
     */
    static public double clip(double num, double lo, double hi) {
        return min(max(num, lo), hi);
    }

    /**
     * Evaluate if a number is significant (absolute value > 0.1)
     */
    static public boolean eval(double num) {
        return abs(num) > 0.0;
    }

    /**
     * Convert boolean to numeric value (true = 1.0, false = 0.0)
     */
    static public double eval(boolean num) {
        return num ? 1.0 : 0.0;
    }
}