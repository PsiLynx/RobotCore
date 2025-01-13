package org.ftc3825.gvf

import org.ftc3825.gvf.GVFConstants.DRIVE_D
import org.ftc3825.gvf.GVFConstants.DRIVE_P
import org.ftc3825.gvf.GVFConstants.HEADING_D
import org.ftc3825.gvf.GVFConstants.HEADING_P
import org.ftc3825.gvf.GVFConstants.HEADING_POW
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.geometry.Rotation2D
import org.ftc3825.util.geometry.Vector2D
import org.ftc3825.util.pid.pdControl

class Path(private var pathSegments: ArrayList<PathSegment>) {
    var index = 0
    val currentPath: PathSegment
        get() = this[index]

    val finishingLast: Boolean
        get() = index >= numSegments

    val numSegments = pathSegments.size

    fun distanceToStop(pos: Vector2D) = (this[-1].end - pos).mag

    operator fun get(i: Int): PathSegment =
        if (i >= numSegments) throw IndexOutOfBoundsException(
            "index $i out of bounds for Path with ${pathSegments.size} paths"
        )
        else if (i >= 0) pathSegments[i]
        else pathSegments[pathSegments.size + i]

    fun pose(currentPose: Pose2D, velocity: Pose2D): Pose2D {

        return if (finishingLast) (
            this[-1].end - currentPose.vector + (
                Rotation2D(HEADING_POW)
                * pdControl(
                    this[-1].targetHeading(1.0),
                    velocity.heading.toDouble(),
                    HEADING_P,
                    HEADING_D
                )
            )
        )
        else {
            val closestT = currentPath.closestT(currentPose.vector)
            val headingError = currentPath.getRotationalError(
                currentPose.heading,
                closestT
            ).toDouble()

            if(currentPath.atEnd && !finishingLast) index ++

            (
                currentPath.getTranslationalVector(currentPose.vector, closestT)
                * pdControl(
                    distanceToStop(currentPose.vector),
                    velocity.vector.mag,
                    DRIVE_P,
                    DRIVE_D
                )
            ) + (
                Rotation2D(HEADING_POW)
                * pdControl(
                    headingError,
                    velocity.heading.toDouble(),
                    HEADING_P,
                    HEADING_D
                )
            )
        }
    }


    override fun toString(): String{

        return (
            "Path: [\n"
                + pathSegments.joinToString("") { "\t$it\n" }
                + "\n]"
        )
    }


}
