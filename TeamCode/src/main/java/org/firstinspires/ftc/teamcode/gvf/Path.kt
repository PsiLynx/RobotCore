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
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

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

        var centripetal = (
            if(
                tangent != Vector2D()
                && closestT < PATH_END_T
                && USE_CENTRIPETAL
            ) (
                ( tangent rotatedBy Rotation2D( PI / 2 ) )
                * tangentVelocity.pow(2)
                * currentPath.curvature(closestT)
            ) else Vector2D()
        ) * CENTRIPETAL

//       if(
//           centripetal != Vector2D()
//           && abs (
//               centripetal.theta.toDouble()
//               - normal.theta.toDouble()
//           ) > degrees(90)
//       ) centripetal.mag -= normalVelocity

        /**
         * tangent vel v_t = MAX_VELO * f_t
         * fraction of power used for centripetal f_c = 1 - f_t = f_t^2 * n
         * 1 - f_t = nf_t^2
         * nf_t^2 + f_t - 1 = 0
         */
        val n = currentPath.Cmax * CENTRIPETAL * MAX_VELO.pow(2)
        val centripetalFraction = if(n != 0.0 && USE_CENTRIPETAL) {
            1 - (sqrt(1 + 4 * n) - 1) / (2 * n)
        } else  0.0

        log("p_closest") value (closest + Rotation2D()).asAkitPose()
        log("p_normal") value ( position.vector + normal.theta ).asAkitPose()
        log("p_tangent") value ( position.vector + tangent.theta ).asAkitPose()
        log("p_centripetal") value ( position.vector + centripetal.theta ).asAkitPose()


        var tan = (tangent.unit * PvState<State.DoubleState>(
            currentPath.distToEnd(position.vector) + (
                 pathSegments.withIndex()
                 .filter { it.index > index }
                 .sumOf { it.value.lenFromT(0.0) }
            ),
            tangentVelocity / MAX_VELO - currentPath.endVelocity,
        ).applyPD(
            DRIVE_P,
            DRIVE_D
        )).coerceIn(0.0, 1.0)

        var norm = PvState(
            normal,
            normal.unit * normalVelocity,
        ).applyPD(
            TRANS_P,
            TRANS_D
        ).coerceIn(0.0, 1.0)

        var head = (
            PvState(
                headingError,
                velocity.heading,
            ).applyPD(
                HEADING_P,
                HEADING_D
            ) * HEADING_POW
        ).coerceIn(0.0, 1.0)

        centripetal *= centripetalFraction

        val scale = (
            tan.mag + norm.mag + abs(head.toDouble())
            / ( 1.0 - centripetalFraction )
        )
        if(scale > 1.0){
            tan  /= scale
            norm /= scale
            head /= scale
        }

        log(" n") value n
        log(" centripetal fraction") value centripetalFraction
        log(" normal pow") value norm.mag
        log(" tangent pow") value tan.mag
        log(" centripetal pow") value centripetal.mag
        log(" closest T") value closestT

        return listOf(
            centripetal + Rotation2D(),

            tan + norm + head
        )
    }


    override fun toString() = (
        "Path: [\n"
            + pathSegments.joinToString("") { "\t$it\n" }
            + "]"
    )

}
