package ro.sparktech24345.logicore.utils

/**
 * Mathematical utility functions for robotics calculations.
 * Provides common operations used in robot control and sensor processing.
 */
class MathUtils {
    companion object {
        /** Calculate absolute value of a number */
        fun abs(num: Double): Double {
            return if (num > 0) num else -num
        }

        /** Get the sign of a number (-1.0, 0.0, or 1.0) */
        fun signum(num: Double): Double {
            return when {
                num < 0.0 -> -1.0
                num > 0.0 -> 1.0
                else -> 0.0
            }
        }

        /** Find the maximum value among multiple numbers */
        fun max(vararg nums: Double): Double {
            require(nums.isNotEmpty()) { "max requires at least one argument" }
            var mx: Double = nums[0]
            for (num in nums) mx = if (mx < num) num else mx
            return mx
        }

        /** Find the minimum value among multiple numbers */
        fun min(vararg nums: Double): Double {
            require(nums.isNotEmpty()) { "min requires at least one argument" }
            var mn: Double = nums[0]
            for (num in nums) mn = if (mn > num) num else mn
            return mn
        }

        /** Clamp a number to be within a specified range */
        fun clip(num: Double, lo: Double, hi: Double): Double {
            return min(max(num, lo), hi)
        }

        /** Evaluate if a number is significant (absolute value > 0.1) */
        fun eval(num: Double): Boolean {
            return abs(num) > .1
        }

        /** Convert boolean to numeric value (true = 1.0, false = 0.0) */
        fun eval(num: Boolean): Double {
            if (num) return 1.0
            return 0.0
        }
    }
}