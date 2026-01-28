package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.params.TrapMpParams
import org.firstinspires.ftc.teamcode.controller.params.TrapMpParams.State
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
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
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
    }

    fun targetPosVelAndAccel(
        position: Pose2D,
        max_vel: Double,
        a_max: Double,
        d_max: Double,
    ): Triple<Pose2D, Pose2D, State> {
        val closestT = currentPath.closestT(position.vector)
        val closestPoint = (
            currentPath.point(closestT)
            + currentPath.targetHeading(closestT)
        )

        val trapMpParams = TrapMpParams(
            currentPath.length,
            max_vel,
            currentPath.v_0 * max_vel + 0.0001,// this is to make sure that
            currentPath.v_f * max_vel + 0.0001,// stuff is non-zero
            a_max,
            d_max
        )
        log("v_0") value currentPath.v_0
        log("v_f") value currentPath.v_f
        log("closest T") value closestT
        log("trap vel") value trapMpParams.velFromX(
            currentPath.length - currentPath.lenFromT(closestT)
        )
        log("trap time") value trapMpParams.t(
            currentPath.length - currentPath.lenFromT(closestT)
        )
        var targetVel = (
            currentPath.velocity(closestT).unit
            * trapMpParams.velFromX(
                currentPath.length
                - currentPath.lenFromT(closestT)
            )
            + Rotation2D()
        )
        targetVel += (
            currentPath.targetHeadingDerivative(closestT)
            * targetVel.vector.mag
            * (
                if(currentPath.heading is HeadingType.ReverseTangent) -1
                else 1
            )
        ) // rotational part

        log("target vel mag") value targetVel.vector.mag
        log("target vel theta") value targetVel.vector.theta.toDouble()


        return Triple(
            closestPoint,
            targetVel,
            trapMpParams.state(
                currentPath.length
                - currentPath.lenFromT(closestT)
            )
        )
    }
    fun gvfPowers(position: Pose2D, velocity: Pose2D): List<Pose2D> {

        var closestT = currentPath.closestT(position.vector)

        if(updateCurrent(closestT)){
            closestT = currentPath.closestT(position.vector)
        }

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

        log("p_closest") value (closest + Rotation2D())
        log("p_normal") value ( position.vector + normal.theta )
        log("p_tangent") value ( position.vector + tangent.theta )
        log("p_centripetal") value ( position.vector + centripetal.theta )


        var tan = (tangent.unit * PvState(
            currentPath.distToEnd(position.vector) + (
                 pathSegments.withIndex()
                 .filter { it.index > index }
                 .sumOf { it.value.length }
            ),
            tangentVelocity / MAX_VELO - currentPath.v_f,
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
            )
        ).coerceIn(-1.0, 1.0)

        centripetal *= centripetalFraction

        log(" n") value n
        log(" heading error") value headingError.toDouble()
        log(" targetHeading") value currentPath.targetHeading(closestT).toDouble()
        log(" currentHeading") value position.heading.toDouble()
        log(" centripetal fraction") value centripetalFraction
        log(" normal pow") value norm.mag
        log(" tangent pow") value tan.mag
        log(" heading pow") value head.toDouble()
        log(" centripetal pow") value centripetal.mag
        log(" closest T") value closestT

        return listOf(
            centripetal + Rotation2D(),
            head + Vector2D(),
            tan + norm + Rotation2D()
        )
    }

    fun updateCurrent(closestT: Double): Boolean {
        if (!finishingLast && currentPath.atEnd(closestT)) {
            index++
            return true
        }
        return false
    }


    override fun toString() = (
        "Path: [\n"
            + pathSegments.joinToString("") { "\t$it\n" }
            + "]"
    )

}
