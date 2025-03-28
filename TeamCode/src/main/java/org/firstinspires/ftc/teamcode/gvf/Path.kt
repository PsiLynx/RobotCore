package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.gvf.GVFConstants.CENTRIPETAL
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.DRIVE_P
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.HEADING_P
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.HEADING_POW
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.MAX_VELO
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.TRANS_P
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.USE_CENTRIPETAL
import org.firstinspires.ftc.teamcode.util.Drawing
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.control.pdControl
import org.firstinspires.ftc.teamcode.util.control.squidControl
import kotlin.math.PI
import kotlin.math.pow

class Path(private var pathSegments: ArrayList<PathSegment>) {
    var index = 0
    val currentPath: PathSegment
        get() = if(index < numSegments) this[index] else this[-1]

    val finishingLast: Boolean
        get() = index >= numSegments

    val numSegments = pathSegments.size

    private fun distanceToStop(pos: Vector2D) = (
        if(index == numSegments - 1) (this[-1].end - pos).mag
        else 10000.0
    )

    operator fun get(i: Int): PathSegment =
        if (i >= numSegments) throw IndexOutOfBoundsException(
            "index $i out of bounds for $this with ${pathSegments.size} paths"
        )
        else if (i >= 0) pathSegments[i]
        else pathSegments[pathSegments.size + i]

    fun powers(position: Pose2D, velocity: Pose2D): List<Pose2D> {
        if(!finishingLast && currentPath.atEnd){
            index ++
        }

        val closestT = currentPath.closestT(position.vector)
        val closest = currentPath.point(closestT)
        Drawing.drawPoint(closest.x, closest.y, "black")
        val headingError = currentPath.getRotationalError(
            position.heading,
            closestT
        )
        val normal = currentPath.getNormalVector(position.vector, closestT)
        val tangent = currentPath.getTangentVector(position.vector, closestT)

        val normalVelocity = velocity.vector.magInDirection(normal.theta)
        val tangentVelocity = velocity.vector.magInDirection(tangent.theta)

        val centripetal = if(tangent != Vector2D() && USE_CENTRIPETAL ) (
            ( tangent rotatedBy Rotation2D( PI / 2 ) )
            * tangentVelocity.pow(2)
            * currentPath.curvature(closestT)
        ) else Vector2D()
        Drawing.drawLine(
            position.x,
            position.y,
            normal.theta.toDouble(),
            "orange"
        )
        Drawing.drawLine(
            position.x,
            position.y,
            tangent.theta.toDouble(),
            "purple"
        )

        return listOf(
            centripetal * CENTRIPETAL + Rotation2D(),
            tangent.unit * squidControl(
                currentPath.distToEnd(position.vector),
                DRIVE_P
            ) + (
                tangent.unit * (
                    currentPath.endVelocity
                    - tangentVelocity / MAX_VELO
                ).coerceIn(-1.0, 1.0)
            ) + squidControl(
                normal,
                TRANS_P,
            ) + squidControl(
                headingError,
                HEADING_P,
            ) * HEADING_POW
        )
    }


    override fun toString() = (
        "Path: [\n"
            + pathSegments.joinToString("") { "\t$it\n" }
            + "]"
    )

}
