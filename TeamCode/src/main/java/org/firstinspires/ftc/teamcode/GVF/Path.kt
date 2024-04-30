package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Vector2D

class Path(vararg var pathSegments: PathSegment) {
    var done = false
    var currentPath = 0
    fun vector(robot: Pose2D): Vector2D {
        with(pathSegments[currentPath]) {
            val closestT = closestT(robot.vector)
            if (closestT == 1.0) {
                currentPath++
                return if (currentPath < pathSegments.size - 2) {
                    vector(robot)
                } else {
                    done = true
                    Vector2D()
                }
            }
            val closest = invoke(closestT)

            val normal = (closest - robot.vector) * PathSegment.AGGRESSIVENESS
            val tangent = derivative(closestT)

            return normal + tangent
        }
    }
}