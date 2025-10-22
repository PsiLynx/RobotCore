package org.firstinspires.ftc.teamcode.geometry

/**
 * represents a "flat" 3D object, or a 2D shape that has a rotation in 3D
 */
interface Shape3D<T: Shape3D<T>> {

    val area: Double
    val normal: Vector3D
    val vertices: Array<Vector3D>

    fun new(verticies: Array<Vector3D>): T
    fun intersectsWith(other: Sphere3D): Boolean
    fun closestPoint(other: Vector3D): Vector3D

    fun translatedBy(other: Vector3D) = new(
        vertices.map { it + other }.toTypedArray()
    )
}