package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Vector2D
import org.firstinspires.ftc.teamcode.util.inches
import org.firstinspires.ftc.teamcode.util.isWithin
import org.firstinspires.ftc.teamcode.util.of
import kotlin.math.abs

class Path(vararg var pathSegments: PathSegment) {
    var decelRadius = inches(6)

    var index = 0
    val currentPath: PathSegment
        get() = this[index]

    val numSegments = pathSegments.size

    operator fun get(i: Int) =
        if (i >= 0) pathSegments[i]
        else pathSegments[pathSegments.size + i]

    fun vector(currentPose: Pose2D): Vector2D {
        val robotLocation = currentPose.vector

        val distanceToEnd = (robotLocation - this[-1].end).mag

        var vector = Vector2D()

        if (index >= numSegments) {
            vector = this[-1].end - robotLocation
        }
        else {
            val it = currentPath

            val closestT = it.closestT(robotLocation)
            if (closestT isWithin 0.05 of 1) {
                index++
                return vector(currentPose)
            }
            else {

                val closestPoint = it(closestT)
                val normal = (closestPoint - robotLocation) * PathSegment.AGGRESSIVENESS
                val tangent = it.tangent(closestT)

                vector = (normal + tangent)
            }
        }
        return vector * (distanceToEnd / decelRadius).coerceIn(0.0, 1.0)
    }

}