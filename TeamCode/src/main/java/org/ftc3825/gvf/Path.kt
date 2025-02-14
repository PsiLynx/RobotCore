package org.ftc3825.gvf

import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.gvf.GVFConstants.DRIVE_D
import org.ftc3825.gvf.GVFConstants.DRIVE_P
import org.ftc3825.gvf.GVFConstants.HEADING_D
import org.ftc3825.gvf.GVFConstants.HEADING_P
import org.ftc3825.gvf.GVFConstants.HEADING_POW
import org.ftc3825.gvf.GVFConstants.TRANS_D
import org.ftc3825.gvf.GVFConstants.TRANS_P
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.Drawing
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.geometry.Rotation2D
import org.ftc3825.util.geometry.Vector2D
import org.ftc3825.util.pid.pdControl
import org.ftc3825.util.pid.pdfControl

class Path(private var pathSegments: ArrayList<PathSegment>) {
    var index = 0
    val currentPath: PathSegment
        get() = if(index < numSegments) this[index] else this[-1]

    val finishingLast: Boolean
        get() = index >= numSegments

    val numSegments = pathSegments.size

    var callbacks = arrayListOf<Pair<Int, InstantCommand>>()


    private fun distanceToStop(pos: Vector2D) = (this[-1].end - pos).mag

    operator fun get(i: Int): PathSegment =
        if (i >= numSegments) throw IndexOutOfBoundsException(
            "index $i out of bounds for $this with ${pathSegments.size} paths"
        )
        else if (i >= 0) pathSegments[i]
        else pathSegments[pathSegments.size + i]

    fun pose(currentPose: Pose2D, velocity: Pose2D): Pose2D {
        if(!finishingLast && currentPath.atEnd){
            index ++
            callbacks.filter { it.first == index }.forEach {
                it.second.schedule()
            }
        }

        val closestT = currentPath.closestT(currentPose.vector)
        val closest = currentPath.point(closestT)
        Drawing.drawPoint(closest.x, closest.y)
        val headingError = currentPath.getRotationalError(
            currentPose.heading,
            closestT
        )
        val normal = currentPath.getNormalVector(currentPose.vector, closestT)
        val tangent = currentPath.getTangentVector(currentPose.vector, closestT)

        val normalVelocity = velocity.vector.magInDirection(normal.theta)
        val tangentVelocity = velocity.vector.magInDirection(tangent.theta)

        Drawing.drawLine(
            Drivetrain.position.x,
            Drivetrain.position.y,
            normal.theta.toDouble(),
            "orange"
        )
        Drawing.drawLine(
            Drivetrain.position.x,
            Drivetrain.position.y,
            tangent.theta.toDouble(),
            "purple"
        )

        return (
            tangent * pdControl(
                distanceToStop(currentPose.vector),
                tangentVelocity,
                DRIVE_P,
                DRIVE_D,
            )
            + pdControl(
                normal,
                normal.unit * normalVelocity,
                TRANS_P,
                TRANS_D,
            )
            + pdControl(
                headingError,
                velocity.heading,
                HEADING_P,
                HEADING_D,
            ) * HEADING_POW
        )
    }


    override fun toString(): String{

        return (
            "Path: [\n"
                + pathSegments.joinToString("") { "\t$it\n" }
                + "]"
        )
    }


}
