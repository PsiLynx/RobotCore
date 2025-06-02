package org.firstinspires.ftc.teamcode.util.geometry

import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.wpi.StructSerializable
import org.firstinspires.ftc.teamcode.wpi.Translation2dStruct
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Vector2D(x: Number = 0.0, y: Number = 0.0): State<Vector2D>(), StructSerializable {
    var x = x.toDouble()
    var y = y.toDouble()


    val magSq: Double
        get() = x * x + y * y

    var mag: Double
        get() = sqrt(magSq)
        set(newMag) {
            if(this == Vector2D()) return
            val scale = (newMag / mag)

            this.x *= scale
            this.y *= scale
        }
    val unit: Vector2D
        get() {
            val output = Vector2D(this.x, this.y)
            output.mag = 1.0
            return output
        }

    val theta: Rotation2D
        get() = Rotation2D(atan2(y, x))

    operator fun unaryPlus() = Vector2D(x, y)
    override operator fun unaryMinus() = Vector2D(-x, -y)

    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    override fun plus(other: State<Vector2D>) = this + (other as Vector2D)
    operator fun plus(other: Rotation2D) = Pose2D(this, other)

    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)

    operator fun times(other: Rotation2D) = Vector2D(
        x * cos(other.toDouble()) - y * sin(other.toDouble()),
        x * sin(other.toDouble()) + y * cos(other.toDouble())
    )
    override operator fun times(scalar: Number) = Vector2D(
        x * scalar.toDouble(), y * scalar.toDouble()
    )
    operator fun times(other: Vector2D) = Vector2D(
        this.x * other.x, this.y * other.y
    )
    override operator fun div(scalar: Number) = this * ( 1 / scalar.toDouble() )

    override fun equals(other: Any?) = (
        other is Vector2D
        && x == other.x
        && y == other.y
    )

    infix fun dot(other: Vector2D) = this.x * other.x + this.y * other.y
    infix fun rotatedBy(angle: Rotation2D) = this * angle

    fun magInDirection(direction: Rotation2D) = if(this != Vector2D()) (
        cos(
            (direction - this.theta).toDouble()
        ) * mag
    ) else 0.0

    override fun toString() = "$x, $y"

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    companion object {
        public val struct = Translation2dStruct()
    }
}