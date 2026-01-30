package org.firstinspires.ftc.teamcode.geometry

import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.geometry.struct.Rotation2DStruct
import org.psilynx.psikit.core.wpi.StructSerializable
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

class Rotation2D(
    theta: Number = 0.0
): State<Rotation2D>, StructSerializable {
    private val theta = theta.toDouble()

    val sign = theta.toDouble().sign
    override val mag get() = theta.toDouble()

    override fun nullState() = Rotation2D()

    operator fun unaryPlus() = Rotation2D(theta)
    override operator fun unaryMinus() = Rotation2D(-theta)

    override operator fun plus(other: Rotation2D)
        = Rotation2D(theta + other.theta)

    operator fun plus(other: Vector2D) = Pose2D(other.x, other.y, theta)

    operator fun minus(other: Rotation2D) = Rotation2D(theta - other.theta)

    operator fun rem(other: Number) = Rotation2D(theta % other.toDouble())
    operator fun times(other: Vector2D) = Vector2D(
        other.x * cos(theta) - other.y * sin(theta),
        other.x * sin(theta) + other.y * cos(theta)
    )
    override operator fun times(other: Number)
        = Rotation2D(theta * other.toDouble())

    /**
     * normalized [0, 2pi)
     */
    fun wrap(): Rotation2D {
        var wrapped = theta

        return Rotation2D(
            ( ( wrapped % (2*PI) ) + (2*PI) ) % (2*PI)
        )
    }

    /**
     * normalized (-pi, pi]
     */
    fun normalized() = Rotation2D(arrayOf(
        (this.wrap()).toDouble(),
        (this.wrap() + Rotation2D(2*PI)).toDouble(),
        (this.wrap() - Rotation2D(2*PI)).toDouble(),
    ).minBy { abs(it) })

    fun absoluteMag() = Rotation2D(arrayOf(
        abs((this.wrap()).toDouble()),
        abs((this.wrap() + Rotation2D(2*PI)).toDouble()),
        abs((this.wrap() - Rotation2D(2*PI)).toDouble()),
    ).minBy { it })

    fun toDouble() = theta

    override fun toString() = theta.toString()

    fun coerceIn(min: Double, max: Double) = (
        if (this.theta > max) Rotation2D(max)
        else if(this.theta < min) Rotation2D(min)
        else this
    )

    fun toInt() = theta.toInt()
    operator fun compareTo(other: Double) = this.theta.compareTo(other)
    operator fun compareTo(other: Int) = this.theta.compareTo(other)
    operator fun compareTo(other: Rotation2D) = this.theta.compareTo(other.theta)

    companion object {
        val struct = Rotation2DStruct()
    }
}