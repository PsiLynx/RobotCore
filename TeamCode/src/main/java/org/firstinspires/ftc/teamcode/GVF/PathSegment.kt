package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Vector2D

open class PathSegment(vararg val controlPoints: Vector2D) {
    val end = controlPoints[controlPoints.size - 1]

    open operator fun invoke(t: Double) = Vector2D()
    open fun derivative(t: Double) = Vector2D()
    fun closest(point: Vector2D) = invoke(closestT(point))
    open fun closestT(point: Vector2D) = 0.0
    fun moveDir(current: Vector2D): Vector2D{
        val closestT = closestT(current)
        val closest = invoke(closestT)

        val normal = (closest - current) * AGGRESSIVENESS
        val tangent = derivative(closestT)

        return normal + tangent
    }
    fun powers(robot: Pose2D) = Pose2D()


    companion object{
        const val AGGRESSIVENESS = 1
        enum class headingPid(){

        }
    }
}