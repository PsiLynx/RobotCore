package org.ftc3825.GVF

import org.ftc3825.GVF.GVFConstants.driveD
import org.ftc3825.GVF.GVFConstants.driveP
import org.ftc3825.GVF.GVFConstants.headingD
import org.ftc3825.GVF.GVFConstants.headingP
import org.ftc3825.GVF.GVFConstants.headingPower
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.Vector2D
import org.ftc3825.util.pid.pdControl

class Path(private vararg var pathSegments: PathSegment) {
    var index = 0
    val currentPath: PathSegment
        get() = this[index]

    val numSegments = pathSegments.size

    operator fun get(i: Int): PathSegment =
        if (i >= numSegments) throw IndexOutOfBoundsException(
            "index $i out of bounds for Path with ${pathSegments.size} paths"
        )
        else if (i >= 0) pathSegments[i]
        else pathSegments[pathSegments.size + i]

    fun pose(currentPose: Pose2D, velocity: Pose2D) =
        (
            if (index >= numSegments) this[-1].end - currentPose.vector
            else{
                if(currentPath.atEnd) index ++
                (
                    currentPath.getTranslationalVector(currentPose.vector)
                        * pdControl(
                            currentPath.fractionComplete,
                            velocity.vector.mag,
                            driveP,
                            driveD
                        )
                ) //return
            }
        ) + Rotation2D(
                pdControl(
                    currentPath.getRotationalError(currentPose.heading).toDouble(),
                    velocity.heading.toDouble(),
                    headingP,
                    headingD
                )
            )


    override fun toString(): String{

        return (
            "Path: [\n"
                + pathSegments.joinToString("") { "\t$it\n" }
                + "\n]"
        )
    }


}
