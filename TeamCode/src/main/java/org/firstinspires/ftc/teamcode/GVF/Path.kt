package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Vector2D

class Path(vararg var Paths: PathSegment) {
    var currentPath = 0
    fun vector(robot: Pose2D): Vector2D{
        with(Paths[currentPath]){
            val closestT = closestT(robot.vector)
            if(closestT == 1.0){
                if(currentPath < Paths.size - 2){
                    currentPath ++
                    return vector(robot)
                }
            }
            val closest = point(closestT)

            val normal = (closest - robot.vector) * PathSegment.aggressiveness
            val tangent = derivative(closestT)

            return normal + tangent
        }
    }
}