package org.firstinspires.ftc.teamcode.util

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Vector2D(x: Number = 0.0, y: Number = 0.0) {
    var _x = x.toDouble()
    var _y = y.toDouble()

    var x: Double
        get() = _x
        set(newX:Double) {
            _x = newX
        }
    var y: Double
        get() = _y
        set(newY:Double) {
            _y = newY
        }

    val magSq = _x * _x + _y * _y
    var mag: Double
        get() = sqrt(magSq)
        set(newMag: Double):Unit {
            val scale = (mag / newMag)
            this.x *= scale
            this.y *= scale
        }
    val unit:Vector2D
        get() = Vector2D(_x / mag, _y / mag)
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
    operator fun div(scalar :Double) = Vector2D(x / scalar, y / scalar)
    override fun equals(other: Any?) = other is Vector2D && x == other.x && y == other.y
    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
    fun normalize() {x /= mag; y /= mag}
    infix fun dot(other: Vector2D) = this.x * other.x + this.y * other.y
    fun rotate(other: Rotation2D) = Vector2D(
    x * cos(other.theta) - y * sin(other.theta),
    x * sin(other.theta) + y * cos(other.theta)
    )

    override fun toString() = "x: $x, y: $y"
}