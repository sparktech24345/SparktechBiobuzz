package ro.sparktech24345.logicore.utils

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 2D vector class with mathematical operations.
 * Currently deprecated as it's not used in the LogiCore codebase.
 * Kept for potential future use or reference.
 */
@Deprecated("Useless for our codebase")
data class Vec2(
    val x: Double,
    val y: Double
) {
    fun rotate(rad: Double): Vec2 {
        val cos = cos(rad)
        val sin = sin(rad)
        return Vec2(
            x * cos - y * sin,
            x * sin + y * cos
        )
    }

    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)

    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)

    operator fun times(scale: Double) = Vec2(x * scale, y * scale)

    operator fun div(scale: Double) = Vec2(x / scale, y / scale)

    operator fun unaryMinus() = Vec2(-x, -y)

    operator fun unaryPlus() = this

    infix fun dot(other: Vec2): Double = x * other.x + y * other.y

    infix fun cross(other: Vec2): Double = x * other.y - y * other.x

    fun lengthSquared(): Double = x * x + y * y

    fun length(): Double = sqrt(lengthSquared())

    fun normalized(): Vec2 {
        val len = length()
        return if (len == 0.0) ZERO else this / len
    }

    fun distanceSquared(other: Vec2): Double = (this - other).lengthSquared()

    fun distance(other: Vec2): Double = sqrt(distanceSquared(other))

    companion object {
        val ZERO = Vec2(0.0, 0.0)
        val ONE = Vec2(1.0, 1.0)

        val UP = Vec2(0.0, 1.0)
        val DOWN = Vec2(0.0, -1.0)

        val LEFT = Vec2(-1.0, 0.0)
        val RIGHT = Vec2(1.0, 0.0)
    }

}

