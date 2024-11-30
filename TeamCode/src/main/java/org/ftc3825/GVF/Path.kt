package org.ftc3825.GVF

import org.ftc3825.util.Pose2D

class Path(private vararg var pathSegments: PathSegment) {
    var index = 0
    val currentPath: PathSegment
        get() = this[index]

    val numSegments = pathSegments.size

    operator fun get(i: Int): PathSegment =
        if (i >= numSegments) throw IndexOutOfBoundsException(
            "index $i out of bounds for Path with ${pathSegments.size} paths"
        )
        else if (i >= 0) pathSegments[i]
        else pathSegments[pathSegments.size + i]

    fun pose(currentPose: Pose2D) =
        (
            if (index >= numSegments) this[-1].end - currentPose.vector
            else{
                if(currentPath.atEnd) index ++
                currentPath.getTranslationalPower(currentPose.vector)
            }
        ) + currentPath.getRotationalPower(currentPose.heading)


    override fun toString(): String{

        return (
            "Path: [\n"
                + pathSegments.joinToString("") { "\t$it\n" }
                + "\n]"
        )
    }


}
