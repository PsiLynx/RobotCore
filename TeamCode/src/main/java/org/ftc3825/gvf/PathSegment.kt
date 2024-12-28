package org.ftc3825.gvf

import org.ftc3825.gvf.GVFConstants.AGGRESSIVENESS
import org.ftc3825.gvf.GVFConstants.PATH_END_T
import org.ftc3825.gvf.HeadingType.Constant
import org.ftc3825.gvf.HeadingType.Linear
import org.ftc3825.gvf.HeadingType.Tangent
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.Vector2D
import kotlin.math.PI

abstract class PathSegment(private vararg var controlPoints: Vector2D, private val heading: HeadingType) {
    val end = controlPoints[controlPoints.size - 1]
    var atEnd = false
        internal set
    var fractionComplete = 0.0
        internal set

    abstract val length: Double

    fun targetHeading(t: Double) = when(heading){
        is Tangent -> tangent(t).theta
        is Constant -> heading.theta
        is Linear -> heading.theta1 * ( 1 - t) + heading.theta2 * t
    }

    abstract fun tangent(t: Double) : Vector2D
    abstract fun closestT(point: Vector2D): Double
    abstract fun point(t: Double): Vector2D

    fun getRotationalError(currentHeading: Rotation2D, t: Double) = Rotation2D(
        (
            targetHeading(t)
            - (
                (
                    (currentHeading.toDouble() + PI / 2)
                    + PI
                ) % ( 2 * PI ) - PI
            )
            + PI
        ) % ( 2 * PI ) - PI //TODO: i don't have any idea WHY this works, ChatGPT wrote it
    )
    fun getTranslationalVector(currentPos: Vector2D, closestT: Double): Vector2D {
        fractionComplete = closestT
        val closestPoint = point(closestT)

        val normal  = (closestPoint - currentPos) * AGGRESSIVENESS
        val tangent = (
            if ( closestT > PATH_END_T ) {
                atEnd = true
                Vector2D()
            } else tangent(closestT).unit
        )

        return normal + tangent
    }

    override fun toString() = "PathSegment: $controlPoints"
}
