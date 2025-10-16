package org.firstinspires.ftc.teamcode.util.geometry

import org.firstinspires.ftc.teamcode.controller.State
import org.psilynx.psikit.core.wpi.StructSerializable
import org.firstinspires.ftc.teamcode.util.geometry.struct.Translation2DStruct
import java.util.Vector
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Vector3D(
    x: Number = 0.0,
    y: Number = 0.0,
    z: Number = 0.0
): State<Vector3D>, StructSerializable {
    var x = x.toDouble()
    var y = y.toDouble()
    var z = z.toDouble()


    val magSq: Double
        get() = x * x + y * y + z * z

    var mag: Double
        get() = sqrt(magSq)
        set(newMag) {
            if(this == Vector3D()) return
            val scale = (newMag / mag)

            this.x *= scale
            this.y *= scale
            this.z *= scale
        }
    val unit: Vector3D
        get() {
            val output = Vector3D(this.x, this.y,this.z)
            output.mag = 1.0
            return output
        }

    val horizontalAngle: Rotation2D
        get() = Rotation2D(atan2(y, x))

    val verticalAngle: Rotation2D
        get() = Rotation2D(atan2(z, mag))

    /**
     * the magnitude is coerced to be within min and max
     */
    fun coerceIn(min: Double, max: Double) = (+this).apply {
        if(mag < min) mag = min
        if(mag > max) mag = max
    }

    override fun nullState() = Vector3D()

    operator fun unaryPlus() = Vector2D(x, y)
    override operator fun unaryMinus() = Vector3D(-x, -y,-z)

    override operator fun plus(other: Vector3D)
            = Vector3D(x + other.x, y + other.y,z + other.z)

    //this needs implemented when a pos3D class gets writen
    //operator fun plus(other: Rotation2D) = Pose2D(this, other)

    operator fun minus(other: Vector3D) = Vector3D(x - other.x, y - other.y,z - other.z)

    //there needs to be a few more arguments about the rotation in 3d space
//    operator fun times(other: Rotation2D) = Vector2D(
//        x * cos(other.toDouble()) - y * sin(other.toDouble()),
//        x * sin(other.toDouble()) + y * cos(other.toDouble())
//    )
    override operator fun times(scalar: Number) = Vector3D(
        x * scalar.toDouble(), y * scalar.toDouble(), z * scalar.toDouble()
    )
    operator fun times(other: Vector3D) = Vector3D(
        this.x * other.x, this.y * other.y, this.z * other.z
    )
    override operator fun div(scalar: Number) = this * ( 1 / scalar.toDouble() )

    override fun equals(other: Any?) = (
            other is Vector3D
                    && x == other.x
                    && y == other.y
                    && z == other.z
            )

    infix fun dot(other: Vector3D) = this.x * other.x + this.y * other.y + this.z * other.z
    //infix fun rotatedBy(angle: Rotation2D) = this * angle

//    fun magInDirection(direction: Rotation2D) = if(this != Vector2D()) (
//            cos(
//                (direction - this.theta).toDouble()
//            ) * mag
//            ) else 0.0

    override fun toString() = "$x, $y, $z"

    //31 is an arbetrary number that is just odd and convienant.
    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

//    companion object {
//        val struct = Translation2DStruct()
//    }
}