package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Vector2D

class Path(vararg var pathSegments: PathSegment) {
    var done = false
    var currentPath = 0
    fun vector(currentPos: Pose2D): Vector2D {
        if(done) return this[-1].end - currentPos.vector
        with(pathSegments[currentPath]) {
            val closestT = closestT(currentPos.vector)
            if (closestT == 1.0) {
                currentPath++
                if (currentPath >= pathSegments.size - 1) {
                    done = true
                }
                return vector(currentPos)
            }
            val closest = invoke(closestT)

            val normal = (closest - currentPos.vector) * PathSegment.AGGRESSIVENESS
            val tangent = derivative(closestT)

            return (normal) //* (6 - (robot.vector - end).mag)
        }
    }

    operator fun get(i: Int) = if (i >= 0) pathSegments[i] else pathSegments[pathSegments.size + i]
}