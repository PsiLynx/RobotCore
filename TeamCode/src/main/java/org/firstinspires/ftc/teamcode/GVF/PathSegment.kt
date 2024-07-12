package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Vector2D

abstract class PathSegment(vararg controlPoints: Vector2D) {
    val end = controlPoints[controlPoints.size - 1]

    abstract fun tangent(t: Double) : Vector2D
    abstract fun closestT(point: Vector2D): Double
    abstract operator fun invoke(t: Double): Vector2D


    companion object{
        const val AGGRESSIVENESS = 2
        enum class headingPid
    }
}