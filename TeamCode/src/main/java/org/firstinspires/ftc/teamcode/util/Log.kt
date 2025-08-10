package org.firstinspires.ftc.teamcode.util

import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.mechanism.LoggedMechanism2d
import org.psilynx.psikit.core.wpi.StructSerializable

interface LoggableName {
    infix fun value(value: StructSerializable)
    infix fun value(value: LoggedMechanism2d)
    infix fun value(value: Array<out StructSerializable>)
    infix fun value(value: Array<out String>)
    infix fun value(value: String)
    infix fun value(value: Number)
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
    override fun value(value: Number) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value.toDouble())
    }
}