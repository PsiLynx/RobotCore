package org.firstinspires.ftc.teamcode.geometry

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * represents a triangle in three dimensions
 * @param A the first corner
 * @param B the second corner
 * @param C the third corner
 */
data class Triangle3D(
    val A: Vector3D,
    val B: Vector3D,
    val C: Vector3D
): Polygon3D<Triangle3D> {

    /*
     coefficients from the function ax + by + cz = d
     this is the standard form for the unique plane that passes through our
     triangle
     */
    val a = (B.y - A.y)*(C.z - A.z) - (B.z - A.z)*(C.y - A.y)
    val b = (B.z - A.z)*(C.x - A.x) - (B.x - A.x)*(C.z - A.z)
    val c = (B.x - A.x)*(C.y - A.y) - (B.y - A.y)*(C.x - A.x)
    val d = a*A.x + b*A.y + c*A.z

    // https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_plane#Restatement_using_linear_algebra
    val v = Vector3D(a, b, c)

    override val normal = v.unit
    override val vertices = arrayOf(A, B, C)

    override fun new(verticies: Array<Vector3D>) = Triangle3D(
        verticies[0],
        verticies[1],
        verticies[2],
    )

    override fun intersectsWith(other: Sphere3D) = (
        ( closestPoint(other.pos) - other.pos ).mag <= other.r
    )

    /**
     * finds the closest point on the triangle to a given point
     * see [wikipedia article on this topic](https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_plane#Restatement_using_linear_algebra)
     */
    override fun closestPoint(other: Vector3D): Vector3D {
        val normalized = Triangle3D(
            A - other,
            B - other,
            C - other,
        )

        val P = ( normalized.v * normalized.d ) / v.magSq + other

        /*
         now, p must lie inside our triangle.
         we will calculate the areas of the three triangles that p_closest
         splits our triangle into, and p lies in this if, and only if, those
         three areas are equal to the area of this.

                    A
                  / | \
                /   P   \
              /  _/   \_  \
            / _/         \_ \
           B/---------------\C

           in this example, the area of ABC = the area of ABP + BCP + ACP,
           but if P was outside ABC, the three "semi-triangles" would be larger.
         */

        val ABP = Triangle3D( A, B, P )
        val BCP = Triangle3D( B, C, P )
        val CAP = Triangle3D( C, A, P )

        val inside = abs(
              this.area
            - ABP.area
            - BCP.area
            - CAP.area
        ) < 1e-3 // dead band for all the floating point errors

        return (
            if (inside) P
            // if the strictly closest point is outside the triangle, we find
            // the edge who's closest point is the closest of all edges, and
            // use that point
            else arrayOf(
                Line3D(A, B).closestPoint(P),
                Line3D(B, C).closestPoint(P),
                Line3D(C, A).closestPoint(P),
            ).minBy { (it - P).mag }
        )

    }

    /**
     * calculates the area of this triangle using heron's formula.
     * @return the area of this triangle
     */
    override val area: Double get(){
        // side lengths
        val a = ( A - B ).mag
        val b = ( B - C ).mag
        val c = ( C - A ).mag

        // semi-perimeter
        val s = 0.5 * ( a + b + c )

        // heron's formula
        return sqrt(
            s * ( s - a ) * ( s - b ) * ( s - c )
        )

    }

    override fun equals(other: Any?) = (
        other is Triangle3D
        && other.vertices.contentEquals(this.vertices)
    )

}