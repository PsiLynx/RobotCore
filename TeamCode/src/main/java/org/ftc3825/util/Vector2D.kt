package org.ftc3825.util

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Vector2D(x: Number = 0.0, y: Number = 0.0) {
    var x = x.toDouble()
    var y = y.toDouble()


    val magSq: Double
        get() = x * x + y * y

    var mag: Double
        get() = sqrt(magSq)
        set(newMag):Unit {
            val scale = (mag / newMag)
            this.x *= scale
            this.y *= scale
        }
    val unit:Vector2D
        get() = Vector2D(x / mag, y / mag)
    operator fun unaryPlus() = Vector2D(x, y)
    operator fun unaryMinus() = Vector2D(-x, -y)
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun plus(other: Rotation2D) = Pose2D(x, y, other.theta)
    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)
    operator fun times(other: Rotation2D) = Vector2D(
        x * cos(other.theta) - y * sin(other.theta),
        x * sin(other.theta) + y * cos(other.theta)
    )
    operator fun times(scalar: Number) = Vector2D(x * scalar.toDouble(), y * scalar.toDouble())
    operator fun div(scalar: Number) = this * ( 1 / scalar.toDouble() )
    override fun equals(other: Any?) = other is Vector2D && x == other.x && y == other.y

    fun normalize() {x /= mag; y /= mag}
    infix fun dot(other: Vector2D) = this.x * other.x + this.y * other.y
    infix fun rotatedBy(angle: Number) = this * Rotation2D(angle)

    override fun toString() = "$x, $y"

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}