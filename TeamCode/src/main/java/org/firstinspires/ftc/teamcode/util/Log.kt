package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.mechanism.LoggedMechanism2d
import org.psilynx.psikit.core.wpi.StructSerializable

interface LoggableName {
    infix fun value(value: StructSerializable)
    infix fun value(value: LoggedMechanism2d)
    infix fun value(value: Array<out StructSerializable>)
    infix fun value(value: Array<out String>)
    infix fun value(value: String)
    infix fun value(value: Boolean)
    infix fun value(value: Number)
    infix fun value(value: PIDFController)
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
    override fun value(value: String) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value)
    }
    override fun value(value: Boolean) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value)
    }
    override fun value(value: Number) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value.toDouble())
    }

    override fun value(value: PIDFController) {
        val path = this@log::class.simpleName + name
        Logger.recordOutput("$path/error", value.setpointError.invoke(value))
        Logger.recordOutput("$path/pos", value.pos())
        Logger.recordOutput("$path/targetPosition", value.targetPosition)
        Logger.recordOutput("$path/feedback", value.feedback)
        Logger.recordOutput("$path/P", value.P())
        Logger.recordOutput("$path/I", value.I())
        Logger.recordOutput("$path/D", value.D())
        Logger.recordOutput("$path/F", value.F(controller.targetPosition, 0.0))
        Logger.recordOutput("$path/G", value.G())
    }
}