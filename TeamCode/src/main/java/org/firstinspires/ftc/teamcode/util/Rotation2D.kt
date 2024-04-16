package org.firstinspires.ftc.teamcode.util

import kotlin.math.cos
import kotlin.math.sin

class Rotation2D(val theta: Double = 0.0) {
    operator fun unaryPlus() = Rotation2D(theta)
    operator fun plus(other:Rotation2D) = Rotation2D(theta + other.theta)
    operator fun plus(other: Vector2D) = Pose2D(other.x, other.y, theta)
    operator fun minus(other: Rotation2D) = Rotation2D(theta - other.theta)
    operator fun times(other: Vector2D) = Vector2D(

        other.x * cos(theta) - other.y * sin(theta),
        other.x * sin(theta) + other.y * cos(theta)
    )
}