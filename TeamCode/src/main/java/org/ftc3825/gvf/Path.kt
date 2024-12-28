package org.ftc3825.gvf

import org.ftc3825.gvf.GVFConstants.DRIVE_D
import org.ftc3825.gvf.GVFConstants.DRIVE_P
import org.ftc3825.gvf.GVFConstants.HEADING_D
import org.ftc3825.gvf.GVFConstants.HEADING_P
import org.ftc3825.gvf.GVFConstants.HEADING_POW
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.pid.pdControl

class Path(private var pathSegments: ArrayList<PathSegment>) {
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

    fun pose(currentPose: Pose2D, velocity: Pose2D): Pose2D {
        val closestT = currentPath.closestT(currentPose.vector)
        return (
            if (index >= numSegments) this[-1].end - currentPose.vector
            else{
                if(currentPath.atEnd) index ++
                (
                    currentPath.getTranslationalVector(currentPose.vector, closestT)
                        * pdControl(
                            currentPath.fractionComplete,
                            velocity.vector.mag,
                            DRIVE_P,
                            DRIVE_D
                        )
                )
            }
        ) + Rotation2D(
                pdControl(
                    currentPath.getRotationalError(
                        currentPose.heading,
                        closestT
                    ).toDouble(),
                    velocity.heading.toDouble(),
                    HEADING_P,
                    HEADING_D
                ) * HEADING_POW
            )
    }


    override fun toString(): String{

        return (
            "Path: [\n"
                + pathSegments.joinToString("") { "\t$it\n" }
                + "\n]"
        )
    }


}
