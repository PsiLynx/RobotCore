package org.ftc3825.GVF

import org.ftc3825.GVF.PathSegment.Companion.AGGRESSIVENESS
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Vector2D
import org.ftc3825.util.inches
import org.ftc3825.util.isWithin
import org.ftc3825.util.of
import kotlin.math.sqrt

class Path(vararg var pathSegments: PathSegment) {
    var decelRadius = 3

    var index = 0
    val currentPath: org.ftc3825.GVF.PathSegment
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

            val closestT = currentPath.closestT(robotLocation)
            if (closestT isWithin 0.05 of 1) {
                index++
                return vector(currentPose)
            } else {

                val closestPoint = currentPath(closestT)
                val normal = (closestPoint - robotLocation) * AGGRESSIVENESS
                val tangent = currentPath.tangent(closestT)

                vector = (normal + tangent)
            }
        }
        return vector * (distanceToEnd / decelRadius).coerceIn(0.0, 1.0)
    }

}
