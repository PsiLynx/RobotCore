package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Vector2D

class Spline(
    val p1: Vector2D,
    val cp1: Vector2D,
    val cp2: Vector2D,
    val p2: Vector2D
): PathSegment(p1, cp1, cp2, p2) {
    private val term3: Vector2D = -p1 * 3 - cp1 * 3 + p2 * 3 - cp2
    private val term4: Vector2D = p1 * 2 + cp1 - p2 * 2

    private val pointsLUT = Array(101) {t: Int -> invoke(t * 0.01)}
    override fun closestT(point: Vector2D) = pointsLUT.indexOf(pointsLUT.minBy { (it - point).magSq }) * 0.01

    override fun invoke(t: Double): Vector2D {
        val tsq = t * t
        return p1 + (cp1 + term3 * t + term4 * tsq) * t // TODO: optimise this line?
        //p1 + cp1 * t + term3 * t^2 + term4 * t^3
    }

    override fun derivative(t: Double): Vector2D {
        return cp1 + term3 * t + term4 * (t * t)
    }

}