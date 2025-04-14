package org.firstinspires.ftc.teamcode.util.geometry

import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

class Rotation2D(theta: Number = 0.0): State<Rotation2D>() {
    private val theta = theta.toDouble()

    val sign = theta.toDouble().sign

    operator fun unaryPlus() = Rotation2D(theta)
    override operator fun unaryMinus() = Rotation2D(-theta)

    operator fun plus(other: Rotation2D) = Rotation2D(theta + other.theta)
    operator fun plus(other: Vector2D) = Pose2D(other.x, other.y, theta)

    override operator fun plus(other: State<Rotation2D>)
        = this + (other as Rotation2D)


    operator fun minus(other: Rotation2D) = Rotation2D(theta - other.theta)

    operator fun times(other: Vector2D) = Vector2D(
        other.x * cos(theta) - other.y * sin(theta),
        other.x * sin(theta) + other.y * cos(theta)
    )
    override operator fun times(other: Number) = Rotation2D(theta * other
        .toDouble())

    fun wrap(): Rotation2D {
        var wrapped = theta
        while (wrapped > Math.PI) wrapped -= 2 * Math.PI
        while (wrapped < -Math.PI) wrapped += 2 * Math.PI
        return Rotation2D(wrapped)
    }
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
}
