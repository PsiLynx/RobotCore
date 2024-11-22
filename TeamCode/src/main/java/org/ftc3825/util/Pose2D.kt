package org.ftc3825.util

import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

class Pose2D(x: Number = 0.0, y: Number = 0.0, heading: Number = 0.0) {
    var x = x.toDouble()
    var y = y.toDouble()
    var heading = heading.toDouble()

    val magSq: Double
        get() = x * x + y * y
    var mag: Double
        get() = sqrt(magSq)
        set(newMag):Unit {
            val scale = (mag / newMag)
            this.x *= scale
            this.y *= scale
        }

    var vector: Vector2D
        get() = Vector2D(x, y)
        set(newVector) {
            x = newVector.x
            y = newVector.y
        }

    operator fun unaryPlus() = Pose2D(x, y, heading)
    operator fun plus(other: Pose2D) = Pose2D(
        x + other.x,
        y + other.y,
        heading + other.heading
    )
    operator fun plus(other: Rotation2D) = Pose2D(x, y, heading + other.theta)
    operator fun minus(other: Pose2D) = Pose2D(
        x - other.x,
        y - other.y,
        heading - other.heading
    )
    operator fun minus(other: Rotation2D) = Pose2D(x, y, heading - other.theta)
    operator fun times(scalar: Double) = Pose2D(x * scalar, y * scalar, heading)
    operator fun div(scalar :Double) = Pose2D(x / scalar, y / scalar, heading)
    override fun equals(other: Any?) = (other is Pose2D) && (x == other.x) && (y == other.y)

    fun unit(): Pose2D = Pose2D(x / mag, y / mag, heading)
    fun reflect(direction: Axis) = when(direction) {
            Axis.XAxis -> Pose2D(-x, y, -heading)
            Axis.YAxis -> Pose2D(x, -y, degrees(180) - heading)
        }

    fun applyToEnd(other: Pose2D) {
        val new = other rotatedBy heading
        this.x += new.x
        this.y += new.y
        this.heading += new.heading
    }

    fun rotate(theta: Double) {
        val originalX = this.x
        val originalY = this.y

        this.x = originalX * cos(theta) - originalY * sin(theta)
        this.y = originalX * sin(theta) + originalY * cos(theta)
        // this.heading += theta
    }

    infix fun rotatedBy(theta: Double) = Pose2D(
        x * cos(theta) - y * sin(theta),
        x * sin(theta) + y * cos(theta),
        heading
    )

    override fun toString() = "x: ${floor(x*1000)/1000.0}, y: ${floor(y*1000)/1000.0}, heading: ${floor(heading*1000)/1000.0}"

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + heading.hashCode()
        return result
    }

    enum class Axis {
        XAxis, YAxis
    }
}