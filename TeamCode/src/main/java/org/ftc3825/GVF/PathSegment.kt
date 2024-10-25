package org.ftc3825.GVF

import org.ftc3825.util.Vector2D

abstract class PathSegment(vararg var controlPoints: Vector2D) {
    val end = controlPoints[controlPoints.size - 1]

    val _endHeading: Double? = null

    open val endHeading: Double
        get(){
            return _endHeading ?: tangent(1.0).theta
        }

    abstract fun tangent(t: Double) : Vector2D
    abstract fun closestT(point: Vector2D): Double
    abstract operator fun invoke(t: Double): Vector2D


    companion object{
        const val AGGRESSIVENESS = 0.1
        const val HEADINGAGGRESSIVENESS = 1
        enum class HeadingPid{
            //FollowPath, SpecificHeading
        }
    }

    override fun toString() = "PathSegment: $controlPoints"
}
