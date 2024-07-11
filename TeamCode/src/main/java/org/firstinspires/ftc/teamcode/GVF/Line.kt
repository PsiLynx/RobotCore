package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Vector2D

class Line(var p1: Vector2D, var p2: Vector2D): PathSegment(p1, p2) {
    constructor(
        x1: Number,
        y1: Number,
        x2: Number,
        y2: Number
    ): this(Vector2D(x1, y1), Vector2D(x2, y2))

    override fun closestT(point: Vector2D): Double{
//        val l1 = (point - p1).mag
//        val alpha = atan2(point._y - p1._y, point._x - p1._x)-atan2(p2._y-p1._y, p2._x-p1._x)
//        val beta = PI/2 - alpha
//        val l2 = sin(beta) * l1 / sin(PI/2)
//        val d = (p2 - p1).mag
//
//	    return l2/d
        val u = p2 - p1
        val v = point - p1

        ( (u dot v) / u.magSq ).let {
            return if (it <= 0.0) 0.0 else if (it >= 1.0) 1.0 else it
        }
    }


    override fun tangent(t: Double) = ( p2 - p1 ).unit
    override fun invoke(t: Double) = p1 * (1 - t) + p2 * t
}
