package org.ftc3825.GVF

import org.ftc3825.GVF.PathSegment.Companion.AGGRESSIVENESS
import org.ftc3825.GVF.PathSegment.Companion.HEADINGAGGRESSIVENESS
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.Vector2D
import org.ftc3825.util.isWithin
import org.ftc3825.util.of
import kotlin.math.PI

class Path(vararg var pathSegments: PathSegment) {
    var decelRadius = 6

    var index = 0
    val currentPath: org.ftc3825.GVF.PathSegment
        get() = this[index]

    val numSegments = pathSegments.size

    operator fun get(i: Int): PathSegment =
        if (i >= numSegments) this[-1]
        else if (i >= 0) pathSegments[i]
        else pathSegments[pathSegments.size + i]

    fun pose(currentPose: Pose2D): Pose2D {
        val robotLocation = currentPose.vector

        val distanceToEnd = (robotLocation - this[-1].end).mag

        var vector = Vector2D()
        var rotation = Rotation2D()

        if (index >= numSegments) {
            vector = this[-1].end - robotLocation
        }
        else {

            val closestT = currentPath.closestT(robotLocation)
            if (closestT isWithin 0.05 of 1) {
                index++
                return pose(currentPose)
            } else {

                val closestPoint = currentPath(closestT)
                val normal = (closestPoint - robotLocation) * AGGRESSIVENESS
                val tangent = currentPath.tangent(closestT)

                vector = (normal + tangent)
                println("==")
                println(currentPath)
                println(tangent.theta)
                println(Drivetrain.position.heading)
            }
        }
        val heading = Drivetrain.position.heading
        rotation = Rotation2D(
            (
                currentPath.endHeading
                -(
                    ( (heading + PI / 2) + PI ) % ( 2 * PI ) - PI
                )
                + PI
            ) % ( 2 * PI ) - PI
        ).coerceIn(-0.2, 0.2)
        return (
            vector.unit * (distanceToEnd / decelRadius).coerceIn(0.15, 0.3)
            + rotation * HEADINGAGGRESSIVENESS
            //+ Rotation2D()
        )
    }

    override fun toString(): String{

        var output = "Path: [\n"
        pathSegments.forEach { output += "\t$it\n" }
        return "$output\n]"
    }


}
