package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Vector2D

class PathSegment(vararg val controlPoints: Vector2D) {

    fun point(t: Double) = Vector2D()
    fun derivative(t: Double) = Vector2D()
    fun closest(point: Vector2D) = Vector2D()
    fun closestT(point: Vector2D) = Vector2D()
    fun vector(point: Vector2D) = Vector2D()
    fun powers(robot: Pose2D) = Pose2D()
    fun distance(point: Vector2D) = 0.0
    fun getEnd() = controlPoints[controlPoints.size - 1]
}