package org.firstinspires.ftc.teamcode.gvf

import org.firstinspires.ftc.teamcode.controller.mp.LerpedConstrainedMP
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.tan

class Arc(
    val start: Vector2D,
    val tangent: Vector2D,
    val direction: Direction,
    val r: Double,
    val theta: Rotation2D,
    override var v_0: Double = 1.0,
    override var v_f: Double = 1.0,
    heading: HeadingType,
): PathSegment(
    start,
    start
        + ( tangent.unit rotatedBy (PI/2*direction.dir) )
        - tangent.unit rotatedBy (
            PI/2*direction.dir + theta.toDouble() *direction.dir
        ),
    heading = heading
) {
    val center = (
        start
        + ( tangent.unit rotatedBy (PI/2*direction.dir) ) * r
    )

    val theta_0 = ( start - center ).theta
    val theta_f = theta_0 + theta * direction.dir
    init {
        controlPoints = arrayOf(
            start,
            point(1.0)
        )
    }

    override fun point(t: Double) = (
        ( (start - center) rotatedBy (theta * t * direction.dir) )
        + center
    )
    override fun velocity(t: Double) = (
        ( tangent.unit rotatedBy ( theta * t * direction.dir ) )
         * theta.toDouble() * r
    )
    override fun accel(t: Double) =
        ( center - point(t) ).unit * theta.toDouble().pow(2) * r

    override fun jerk(t: Double) = (
        ( tangent.unit rotatedBy ( theta * t * direction.dir ) )
        * ( -theta.toDouble().pow(3) * r * direction.dir )
    )

    override fun lenFromT(t: Double) = theta.toDouble() * r * (1 - t)

    override fun closestT(point: Vector2D): Double {
        val theta_point = (point - center).theta.normalized()

        val lowerCloser = (
            (theta_point - theta_0).absoluteMag()
            < (theta_point - theta_f).absoluteMag()
        )
        if(
            lowerCloser
            && (theta_point - theta_0).normalized() * direction.dir < 0
        ){
            return 0.0
        }
        if(
            !lowerCloser
            && (theta_point - theta_f).normalized() * direction.dir > 0
        ){
            return 1.0
        }
        return (
            (theta_point - theta_0).normalized().toDouble() * direction.dir
            / theta.toDouble()
        )
    }

    //override fun curvature(t: Double) = 1 / r * direction.dir

    override val Cmax: Double = 1/r
    override fun toString() =
        "Arc: r: ${floor(r * 100) / 100} $center, $theta_0 -> $theta_f"

    enum class Direction(val dir: Int) {
        LEFT(1), RIGHT(-1);
        operator fun times(other: Int) = (
            if(other == 1) this
            else if(other == -1){
                if(this == LEFT) RIGHT
                else LEFT
            }
            else error("must multiply Arc.Direction by -1 or 1 (got $other)")
        )
    }

}