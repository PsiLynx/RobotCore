package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Vector2D

class Path(vararg var pathSegments: PathSegment) {
    var done = false
    var currentPath = 0
    fun vector(robot: Pose2D): Vector2D{
        with(pathSegments[currentPath]){
            val closestT = closestT(robot.vector)
            if(closestT == 1.0){
                if(currentPath < pathSegments.size - 2){
                    currentPath ++
                    return vector(robot)
                }
                else{
                    currentPath ++
                    done = true
                    return Vector2D()
                }
            }
            val closest = invoke(closestT)

            val normal = (closest - robot.vector) * PathSegment.AGGRESSIVENESS
            val tangent = derivative(closestT)

            return normal + tangent
        }
    }
}