package org.firstinspires.ftc.teamcode.util.geometry

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.util.geometry.struct.Pose2DStruct
import org.psilynx.psikit.core.wpi.StructSerializable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Pose2D(
    var vector: Vector2D,
    var heading: Rotation2D
): State<Pose2D>, StructSerializable {
    constructor(x: Number = 0.0, y: Number = 0.0, heading: Number = 0.0): this(
        Vector2D(x, y), Rotation2D(heading)
    )
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

    fun asAkitPose() = Pose2D(
        this.vector * 0.0254,
        this.heading
    )
    fun asSDKPose() = SDKPose(
        DistanceUnit.INCH,
        x, y,
        AngleUnit.RADIANS,
        heading.toDouble()
    )

    /**
     * deep copy, no references to original
     */
    operator fun unaryPlus() = Pose2D(x, y, heading.toDouble())
    override fun nullState() = Pose2D()


    operator fun minus(other: Pose2D) = Pose2D(
        this.vector - other.vector,
        this.heading - other.heading
    )
    operator fun plus(other: Rotation2D) = Pose2D(vector, heading + other)
    operator fun plus(other: Vector2D) = Pose2D(vector + other, this.heading)
    override operator fun plus(other: Pose2D) = Pose2D(
        (vector + other.vector),
        (heading + other.heading)
    )


    operator fun minus(other: Rotation2D) = Pose2D(vector, heading - other)
    operator fun minus(other: Vector2D) = Pose2D(vector - other, heading)

    override operator fun times(other: Number) = this * other.toDouble()
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

    override fun toString() = ( "Pose2D(" +
        "${(x*1000).toInt()/1000.0}, " +
        "${(y*1000).toInt()/1000.0}, " +
        "${(heading*1000).toInt()/1000.0}" + ")"
    )

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + heading.hashCode()
        return result
    }

    companion object {
        @JvmField
        val struct = Pose2DStruct()
    }
}