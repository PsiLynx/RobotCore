package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Vector2D

class Spline(var p1: Vector2D, var cp1: Vector2D, var cp2: Vector2D, var p2: Vector2D): PathSegment(p1, cp1, cp2, p2) {
    val term3: Vector2D = -p1 * 3 - cp1 * 3 + p2 * 3 - cp2
    val term4: Vector2D = p1 * 2 + cp1 - p2 * 2
    override fun closestT(point: Vector2D): Double {
        var minDist = Double.MAX_VALUE
        var minT = 0.0
        for(i in 0..100){
            val t = i * 0.01
            val currentDist = (point(t) - point).magSq
            if(currentDist < minDist){
                minDist = currentDist
                minT = t
            }
        }

        return minT
    }

    override fun point(t: Double): Vector2D {
        val tsq = t * t
        return p1 + (cp1 + term3 * t + term4 * tsq) * t // TODO: optimise this line?
        //p1 + cp1 * t + term3 * t^2 + term4 * t^3
    }

    override fun derivative(t: Double): Vector2D {
        return cp1 + term3 * t + term4 * (t * t)
    }

}