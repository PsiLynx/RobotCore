package org.ftc3825.util.geometry

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import kotlin.math.cos
import kotlin.math.sin

class Pose2D(var vector: Vector2D, var heading: Rotation2D) {
    constructor(x: Number = 0.0, y: Number = 0.0, heading: Number = 0.0): this(
        Vector2D(x, y), Rotation2D(heading)
    )
//    constructor(
//        position: org.firstinspires.ftc.robotcore.external.navigation.Pose2D
//    ) : this(
//        position.getX(DistanceUnit.INCH),
//        position.getY(DistanceUnit.INCH),
//        position.getHeading(AngleUnit.RADIANS)
//    )

    var x: Double
        get() = vector.x
        set(value){ vector = Vector2D(value, vector.y) }

    var y: Double
        get() = vector.y
        set(value){ vector = Vector2D(vector.x, value) }

    val magSq: Double
        get() = vector.magSq
    var mag: Double
        get() = vector.mag
        set(value) { vector.mag = value }

    operator fun unaryPlus() = Pose2D(vector, heading)

    operator fun minus(other: Pose2D) = Pose2D(
        this.vector - other.vector,
        this.heading - other.heading
    )
    operator fun plus(other: Rotation2D) = Pose2D(vector, heading + other)
    operator fun plus(other: Vector2D) = Pose2D(vector + other, this.heading)
    operator fun plus(other: Pose2D) = Pose2D(
        (vector + other.vector),
        (heading + other.heading)
    )

    operator fun minus(other: Rotation2D) = Pose2D(vector, heading - other)
    operator fun minus(other: Vector2D) = Pose2D(vector - other, heading)

    operator fun times(scalar: Double) = Pose2D(vector * scalar, heading)
    operator fun div(scalar: Double) = Pose2D(vector / scalar, heading)

    override fun equals(other: Any?) = (other is Pose2D) && vector == other.vector && heading == other.heading

    fun unit(): Pose2D = Pose2D(vector.unit, heading)

    fun applyToEnd(other: Pose2D) {
        val new = other rotatedBy heading
        this.x += new.x
        this.y += new.y
        this.heading += new.heading
    }

    fun rotate(angle: Rotation2D) {
        val originalX = this.x
        val originalY = this.y

        this.x = originalX * cos(angle.toDouble()) - originalY * sin(angle.toDouble())
        this.y = originalX * sin(angle.toDouble()) + originalY * cos(angle.toDouble())
    }

    infix fun rotatedBy(angle: Rotation2D) = Pose2D(
        vector rotatedBy angle,
        heading
    )

    fun asSDKPose()= org.firstinspires.ftc.robotcore.external.navigation.Pose2D(
        DistanceUnit.INCH,
        x,
        y,
        AngleUnit.RADIANS,
        heading.toDouble()
    )

    override fun toString() = (
        "x: ${(x*1000).toInt()/1000.0}, " +
        "y: ${(y*1000).toInt()/1000.0}, " +
        "heading: ${(heading*1000).toInt()/1000.0}"
    )

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + heading.hashCode()
        return result
    }

}