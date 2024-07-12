package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Vector2D

abstract class PathSegment(vararg controlPoints: Vector2D) {
    val end = controlPoints[controlPoints.size - 1]

    abstract fun tangent(t: Double) : Vector2D
    abstract fun closestT(point: Vector2D): Double
    abstract operator fun invoke(t: Double): Vector2D

    fun closest(point: Vector2D) = invoke(closestT(point))

    fun moveDir(current: Vector2D): Vector2D{
        val closestT = closestT(current)
        val closest = invoke(closestT)

        val normal = (closest - current) * AGGRESSIVENESS
        val tangent = tangent(closestT)

        return normal + tangent
    }


    companion object{
        const val AGGRESSIVENESS = 2
        enum class headingPid
    }
}