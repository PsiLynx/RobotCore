package org.ftc3825.GVF

import org.ftc3825.util.Vector2D

abstract class PathSegment(vararg controlPoints: Vector2D) {
    val end = controlPoints[controlPoints.size - 1]

    abstract fun tangent(t: Double) : Vector2D
    abstract fun closestT(point: Vector2D): Double
    abstract operator fun invoke(t: Double): Vector2D


    companion object{
        const val AGGRESSIVENESS = 2
        enum class HeadingPid
    }
}