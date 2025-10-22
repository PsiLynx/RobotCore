package org.firstinspires.ftc.teamcode.geometry

/**
 * represents a line (segment) in three dimensions
 * @param A the start point of the segment
 * @param B the end point of the segment
 */
data class Line3D(
    val A: Vector3D,
    val B: Vector3D,
){
    /**
     * returns the closest point on this segment to another point.
     * @param other the point to find the closest point to
     */
    fun closestPoint(other: Vector3D): Vector3D{
        val u = B - A
        val v = other - A
        // T, the fraction along the line from B to A, at which the closest
        // point is found
        val closest_T = ( (u dot v) / u.magSq ).coerceIn(0.0, 1.0)

        return A + u * closest_T


    }
}
