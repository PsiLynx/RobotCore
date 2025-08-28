package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.CENTRIPETAL
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.DRIVE_D
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.DRIVE_P
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.HEADING_P
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.HEADING_D
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.MAX_VELO
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.PATH_END_T
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.TRANS_D
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.TRANS_P
import org.firstinspires.ftc.teamcode.gvf.GVFConstants.USE_CENTRIPETAL
import org.firstinspires.ftc.teamcode.sim.FakeGVFConstants.HEADING_POW
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.gversion.VERSION
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow

class Path(private val pathSegments: ArrayList<PathSegment>) {
    var index = 0
    val currentPath: PathSegment
        get() = if(index < numSegments) this[index] else this[-1]

    val finishingLast: Boolean
        get() = index >= numSegments

    val numSegments = pathSegments.size

    operator fun get(i: Int): PathSegment =
        if (i >= numSegments) throw IndexOutOfBoundsException(
            "index $i out of bounds for $this with ${pathSegments.size} paths"
        )
        else if (i >= 0) pathSegments[i]
        else pathSegments[pathSegments.size + i]

    fun reset(){
        index = 0
        pathSegments.forEach { it.reset() }
    }
    fun powers(position: Pose2D, velocity: Pose2D): List<Pose2D> {
        if(!finishingLast && currentPath.atEnd){
            index ++
        }

        val closestT = currentPath.closestT(position.vector)
        val closest = currentPath.point(closestT)
        val headingError = currentPath.getRotationalError(
            position.heading,
            closestT
        )
        val normal = currentPath.getNormalVector(position.vector, closestT)
        val tangent = currentPath.getTangentVector(position.vector, closestT)


        val normalVelocity = velocity.vector.magInDirection(normal.theta)
        val tangentVelocity = velocity.vector.magInDirection(tangent.theta)

        var centripetal = if(
            tangent != Vector2D()
            && closestT < PATH_END_T
            && USE_CENTRIPETAL
        ) (
            ( tangent rotatedBy Rotation2D( PI / 2 ) )
            * tangentVelocity.pow(2)
            * currentPath.curvature(closestT)
        ) else Vector2D()
        log("curvature") value currentPath.curvature(closestT)

        log("normal") value ( position.vector + normal.theta ).asAkitPose()
        log("tangent") value ( position.vector + tangent.theta ).asAkitPose()
        log("centripetal") value ( position.vector + centripetal.theta ).asAkitPose()


        val tan = tangent.unit * PvState<State.DoubleState>(
            currentPath.distToEnd(position.vector) + (
                 pathSegments.withIndex()
                 .filter { it.index > index }
                 .sumOf { it.value.lenFromT(0.0) }
            ),
            tangentVelocity / MAX_VELO - currentPath.endVelocity,
        ).applyPD(
            DRIVE_P,
            DRIVE_D
        )
        val norm = PvState(
            normal,
            normal.unit * normalVelocity,
        ).applyPD(
            TRANS_P,
            TRANS_D
        )
        val head = PvState(
            headingError,
            velocity.heading,
        ).applyPD(
            HEADING_P,
            HEADING_D
        ) * HEADING_POW
        log("normal pow") value norm.mag
        log("tangent pow") value tan.mag
        log("centripetal pow") value centripetal.mag * CENTRIPETAL
        log("closest T") value closestT
        log("closest") value (closest + Rotation2D()).asAkitPose()

        return listOf(
            centripetal * CENTRIPETAL + Rotation2D(),

            tan + norm + head
        )
    }


    override fun toString() = (
        "Path: [\n"
            + pathSegments.joinToString("") { "\t$it\n" }
            + "]"
    )

}
