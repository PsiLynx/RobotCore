package org.ftc3825.GVF

import org.ftc3825.GVF.GVFConstants.AGGRESSIVENESS
import org.ftc3825.GVF.GVFConstants.pathEndTValue
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.Vector2D
import kotlin.math.PI

abstract class PathSegment(private vararg var controlPoints: Vector2D) {
    val end = controlPoints[controlPoints.size - 1]
    var atEnd = false
        internal set

    private var _endHeading: Double? = null

    open val endHeading: Double
        get(){
            return _endHeading ?: tangent(1.0).theta
        }

    abstract fun tangent(t: Double) : Vector2D
    abstract fun closestT(point: Vector2D): Double
    abstract fun point(t: Double): Vector2D


    fun getRotationalPower(currentHeading: Rotation2D) = Rotation2D(
        (
            endHeading
            - (
                (
                    (currentHeading.toDouble() + PI / 2)
                    + PI
                ) % ( 2 * PI ) - PI
            )
            + PI
        ) % ( 2 * PI ) - PI //TODO: i don't have any idea WHY this works, ChatGPT wrote it
    )
    fun getTranslationalPower(currentPos: Vector2D): Vector2D {
        val closestT = closestT(currentPos)
        val closestPoint = point(closestT)

        val normal  = (closestPoint - currentPos) * AGGRESSIVENESS
        val tangent = if ( closestT > pathEndTValue ) {
            atEnd = true
            Vector2D()
        } else tangent(closestT)

        return normal + tangent
    }

    override fun toString() = "PathSegment: $controlPoints"
}
