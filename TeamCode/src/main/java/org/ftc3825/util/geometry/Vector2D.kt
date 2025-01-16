package org.ftc3825.util.geometry

import org.ftc3825.pedroPathing.pathGeneration.Vector
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Vector2D(x: Number = 0.0, y: Number = 0.0) {
    constructor(vector: Vector): this(vector.xComponent, vector.yComponent)
    var x = x.toDouble()
    var y = y.toDouble()


    val magSq: Double
        get() = x * x + y * y

    var mag: Double
        get() = sqrt(magSq)
        set(newMag):Unit {
            val scale = (newMag / mag)
            this.x *= scale
            this.y *= scale
        }
    val unit: Vector2D
        get() = Vector2D(x / mag, y / mag)

    val theta: Rotation2D
        get() = Rotation2D(atan2(y, x))

    operator fun unaryPlus() = Vector2D(x, y)
    operator fun unaryMinus() = Vector2D(-x, -y)
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun plus(other: Rotation2D) = Pose2D(this, other)
    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)
    operator fun times(other: Rotation2D) = Vector2D(
        x * cos(other.toDouble()) - y * sin(other.toDouble()),
        x * sin(other.toDouble()) + y * cos(other.toDouble())
    )
    operator fun times(scalar: Number) = Vector2D(x * scalar.toDouble(), y * scalar.toDouble())
    operator fun div(scalar: Number) = this * ( 1 / scalar.toDouble() )
    override fun equals(other: Any?) = other is Vector2D && x == other.x && y == other.y

    fun normalize() {x /= mag; y /= mag}
    infix fun dot(other: Vector2D) = this.x * other.x + this.y * other.y
    infix fun rotatedBy(angle: Rotation2D) = this * angle

    fun magInDirection(direction: Rotation2D) = (
        cos(
            (direction - this.theta).toDouble()
        ) * mag
    )

    override fun toString() = "$x, $y"

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}