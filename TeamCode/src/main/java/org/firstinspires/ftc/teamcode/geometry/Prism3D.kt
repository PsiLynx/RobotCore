package org.firstinspires.ftc.teamcode.geometry

data class Prism3D(
    val top: Polygon3D<*>,
    val height: Double
) {
    val bottom = top.translatedBy(top.normal * (-height))
    lateinit var faces: Array<Polygon3D<*>>
    init {
        val facesList = arrayListOf(
            top, bottom
        )
        repeat(top.vertices.size) { i ->
            facesList.add(Quad3D(
                top   .vertices[ i ],
                top   .vertices[ (i + 1) % top.vertices.size ],
                bottom.vertices[ (i + 1) % top.vertices.size ],
                bottom.vertices[ i ],
            ))
        }
        faces = facesList.toTypedArray()
    }
    fun intersectsWith(other: Sphere3D) =
        faces.find { it.intersectsWith(other) } != null

    fun intersectingFaces(other: Sphere3D) = faces.filter {
        it.intersectsWith(other)
    }

    override fun equals(other: Any?) = (
        other is Prism3D
        && other.top == this.top
        && other.height == this.height
    )
}
