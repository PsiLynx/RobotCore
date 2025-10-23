package org.firstinspires.ftc.teamcode.geometry

/**
 * represents a 3D quadrilateral. note that this could technically have a
 * "bend" in the middle, it's required that this doesn't happen
 * (within floating point error tolerances)
 * @param A corner 1
 * @param B corner 2
 * @param C corner 3
 * @param D corner 4
 */
data class Quad3D(
    val A: Vector3D,
    val B: Vector3D,
    val C: Vector3D,
    val D: Vector3D,
): Shape3D<Quad3D> {

    override val normal get() = Triangle3D(A, B, C).normal
    override val vertices = arrayOf(A, B, C, D)

    override fun new(verticies: Array<Vector3D>) = Quad3D(
        verticies[0],
        verticies[1],
        verticies[2],
        verticies[3],
    )

    override fun intersectsWith(other: Sphere3D) = (
           Triangle3D(A, B, C).intersectsWith(other)
        || Triangle3D(C, D, A).intersectsWith(other)
    )

    override fun closestPoint(other: Vector3D) = arrayOf(
        Triangle3D(A, B, C).closestPoint(other),
        Triangle3D(C, D, A).closestPoint(other)
    ).minBy { ( it - other).mag }

    override val area get() = (
          Triangle3D(A, B, C).area
        + Triangle3D(C, D, A).area
    )

}
