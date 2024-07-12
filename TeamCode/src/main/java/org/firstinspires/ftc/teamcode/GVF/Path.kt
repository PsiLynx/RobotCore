package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Pose2D
import org.firstinspires.ftc.teamcode.util.Vector2D
import org.firstinspires.ftc.teamcode.util.inches

class Path(vararg var pathSegments: PathSegment) {
    var currentPath = 0

    val length:Int
        get() = pathSegments.size
    fun vector(currentPos: Pose2D): Vector2D {
        val slowdown = inches(6)
        val dist = (currentPos.vector - this[-1].end).magSq

        return (
                if(currentPath > pathSegments.size - 1){
                    this[-1].end - currentPos.vector
                } else {
                    with(pathSegments[currentPath]) {
                        val closestT = closestT(currentPos.vector)
                        if (closestT == 1.0) {
                            currentPath++
                        }

                        val closest = invoke(closestT)

                        val normal = (closest - currentPos.vector) * PathSegment.AGGRESSIVENESS
                        val tangent = tangent(closestT)

                        (normal + tangent)
                    }
                }
        ) * if (dist < slowdown) dist / slowdown else 1
    }

    operator fun get(i: Int) = if (i >= 0) pathSegments[i] else pathSegments[pathSegments.size + i]
}