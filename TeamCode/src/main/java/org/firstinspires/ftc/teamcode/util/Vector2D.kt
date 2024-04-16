package org.firstinspires.ftc.teamcode.util

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Vector2D(var x: Double = 0.0, var y: Double = 0.0) {
    val magSq: Double
        get() = x * x + y * y
    var mag: Double
        get() = sqrt(magSq)
        set(newMag: Double):Unit {
            val scale = (mag / newMag)
            this.x *= scale
            this.y *= scale
        }

    operator fun unaryPlus() = Vector2D(x, y)
    operator fun unaryMinus() = Vector2D(-x, -y)
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun plus(other: Rotation2D) = Pose2D(x, y, other.theta)
    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)
    operator fun times(other: Rotation2D) = Vector2D(
        x * cos(other.theta) - y * sin(other.theta),
        x * sin(other.theta) + y * cos(other.theta)
    )
    operator fun times(scalar: Double) = Vector2D(x * scalar, y * scalar)
    operator fun div(scalar :Double) = Vector2D(x / scalar, y / scalar)
    override fun equals(other: Any?) = other is Vector2D && x == other.x && y == other.y
    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    fun unit(): Vector2D = Vector2D(x / mag, y / mag)
}