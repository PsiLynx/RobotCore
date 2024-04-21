package org.firstinspires.ftc.teamcode.util

import java.lang.IllegalStateException
import kotlin.math.sqrt

class Pose2D(var x: Double = 0.0, var y: Double = 0.0, var heading: Double = 0.0) {
    val magSq: Double
        get() = x * x + y * y
    var mag: Double
        get() = sqrt(magSq)
        set(newMag: Double):Unit {
            val scale = (mag / newMag)
            this.x *= scale
            this.y *= scale
        }

    var vector: Vector2D
        get() = Vector2D(x, y)
        set(newVector: Vector2D) {
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
        heading + other.heading
    )
    operator fun minus(other: Rotation2D) = Pose2D(x, y, heading - other.theta)
    operator fun times(scalar: Double) = Pose2D(x * scalar, y * scalar)
    operator fun div(scalar :Double) = Pose2D(x / scalar, y / scalar)
    override fun equals(other: Any?) = (other is Pose2D) && (x == other.x) && (y == other.y)

    fun unit(): Pose2D = Pose2D(x / mag, y / mag)
    fun reflect(direction: Int):Pose2D {

        return when(direction) {
            Pose2D.Xaxis -> Pose2D(-x, y, -heading)
            Pose2D.Yaxis -> Pose2D(x, -y, 180 - heading)
            else -> throw IllegalStateException("direction in Pose2D.reflect must be 0 or 1 (from the companion class)")
        }
    }
    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + heading.hashCode()
        return result
    }

    companion object{
        const val Xaxis: Int = 0
        const val Yaxis: Int = 1
    }
}