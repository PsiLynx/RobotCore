package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.akit.Logger
import org.firstinspires.ftc.teamcode.akit.mechanism.LoggedMechanism2d
import org.firstinspires.ftc.teamcode.akit.wpi.StructSerializable

interface LoggableName {
    infix fun value(value: StructSerializable)
    infix fun value(value: LoggedMechanism2d)
    infix fun value(value: Array<out StructSerializable>)
}
fun Any.log(name: String) = object : LoggableName {
    override fun value(value: StructSerializable) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value)
    }
    override fun value(value: LoggedMechanism2d) {
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value)
    }
    override fun value(value: Array<out StructSerializable>) {
        println(this@log::class.simpleName)
        Logger.recordOutput(this@log::class.simpleName + "/" + name, value)
    }
}