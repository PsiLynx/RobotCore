package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.geometry.Prism3D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Sphere3D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.mechanism.LoggedMechanism2d
import org.psilynx.psikit.core.wpi.StructSerializable
import kotlin.math.cos
import kotlin.math.sin

interface LoggableName {
    infix fun value(value: StructSerializable)
    infix fun value(value: LoggedMechanism2d)
    infix fun value(value: Array<out StructSerializable>)
    infix fun value(value: Array<out String>)
    infix fun value(value: Array<out Double>)
    infix fun value(value: String)
    infix fun value(value: Boolean)
    infix fun value(value: Number)
    infix fun value(value: Rotation2D)
    infix fun value(value: PIDFController)
    infix fun value(value: Prism3D)
    infix fun value(value: Sphere3D)
    infix fun value(value: ChassisSpeeds)
    infix fun value(value: Command)
}
fun Any.log(name: String) = object : LoggableName {
    override fun value(value: StructSerializable) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value)
    }
    override fun value(value: LoggedMechanism2d) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value)
    }
    override fun value(value: Array<out StructSerializable>) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value)
    }
    override fun value(value: Array<out String>) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value)
    }
    override fun value(value: Array<out Double>) {
        Logger.recordOutput(
            this@log::class.simpleName + "/" + name,
            value.toDoubleArray()
        )
    }
    override fun value(value: String) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value)
    }
    override fun value(value: Boolean) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value)
    }
    override fun value(value: Number) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value.toDouble())
    }
    override fun value(value: Rotation2D) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value.toDouble())
    }

    override fun value(value: ChassisSpeeds) {
        val name = this@log::class.simpleName + "/" + name
        Logger.recordOutput("$name/vx", value.vx / 39.37 )
        Logger.recordOutput("$name/vy", value.vy / 39.37 )
        Logger.recordOutput("$name/vTheta", value.vTheta )
    }
    override fun value(value: Sphere3D) {
        val latSteps = 12
        val lonSteps = 18
        val points = mutableListOf<Vector3D>()
        for (i in 0 until latSteps) {
            val phi = Math.PI * i / (latSteps - 1)
            val ring = mutableListOf<Vector3D>()
            for (j in 0 ..< lonSteps) {
                val theta = 2 * Math.PI * j / lonSteps
                val px = value.r * sin(phi) * cos(theta)
                val py = value.r * sin(phi) * sin(theta)
                val pz = value.r * cos(phi)
                ring += Vector3D(px, py, pz) + value.pos
            }
            points += if (i % 2 == 0) ring else ring.reversed()
        }
        Logger.recordOutput(
            this@log::class.simpleName + "/" + name,
            points.toTypedArray()
        )
    }

    override fun value(value: Prism3D) {
        Logger.recordOutput(
            this@log::class.simpleName + "/" + name,
            (
                value.top.vertices + value.top.vertices[0]
                + value.bottom.vertices + value.bottom.vertices[0]
            )
        )
    }

    override fun value(value: PIDFController) {
        val path = this@log::class.simpleName + "/" + name
        Logger.recordOutput("$path/error", value.setpointError.invoke(value))
        Logger.recordOutput("$path/pos", value.pos())
        Logger.recordOutput("$path/targetPosition", value.targetPosition)
        Logger.recordOutput("$path/feedback", value.feedback)
        Logger.recordOutput("$path/P", value.P())
        Logger.recordOutput("$path/I", value.I())
        Logger.recordOutput("$path/D", value.D())
        Logger.recordOutput("$path/F", value.F(value.targetPosition, 0.0))
        Logger.recordOutput("$path/G", value.G())
    }
    override fun value(value: Command) {
        Logger.recordOutput(
            this@log::class.simpleName + "/" + name,
            value.toString()
        )
    }
}