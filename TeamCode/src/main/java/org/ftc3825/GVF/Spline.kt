package org.ftc3825.GVF

import org.ftc3825.GVF.GVFConstants.pointsInLUT
import org.ftc3825.GVF.GVFConstants.splineResolution
import org.ftc3825.util.Vector2D
import kotlin.math.ceil

class Spline(
    val p1: Vector2D,
    val cp1: Vector2D,
    val cp2: Vector2D,
    val p2: Vector2D,
    heading: HeadingType
): PathSegment(p1, cp1, cp2, p2, heading = heading) {
    constructor(
        x1:Number, y1:Number,
        cx1:Number, cy1:Number,
        x2:Number, y2:Number,
        cx2:Number, cy2:Number,
        heading: HeadingType
    ) : this(
        Vector2D(x1, y1),
        Vector2D(cx1, cy1),
        Vector2D(cx2, cy2),
        Vector2D(x2, y2),
        heading = heading
    )

    private val v1 = p1 + cp1
    private val v2 = p2 - cp2

    private val coef = Array(4) {
        when (it) {
            0 ->  p1
            1 -> -p1*3.0   + v1*3.0
            2 ->  p1*3.0   - v1*6.0 + v2*3.0
            3 -> -p1       + v1*3.0 - v2*3.0 + p2
            else -> Vector2D()
        }
    }
    private val pointsLUT = (
        Array(pointsInLUT.toInt() + 1) { t -> point(t / pointsInLUT) }
    )

    override val length = arrayListOf(*pointsLUT).fold(0.0 to p1) { acc, point ->
        (point - acc.second).mag to point
    }.first // accumulate length in acc.first

    override fun closestT(point: Vector2D) = (
        pointsLUT.indexOf(
            pointsLUT.minBy { (it - point).magSq }
        ) / pointsInLUT
        ).coerceIn(0.0, 1.0)
    override fun point(t: Double): Vector2D {
        val tsq = t * t
        return (
              coef[0]
            + coef[1] * ( t )
            + coef[2] * ( t * t )
            + coef[3] * ( t * t * t )
        )
    }
    override fun tangent(t: Double): Vector2D {
        return (
              coef[1]
            + coef[2] * t
            + coef[3] * (t * t)
            ).unit
    }

    override fun toString() = "Spline: ($p1), ($p2)"
}
