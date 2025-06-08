package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.teamcode.akit.Logger
import org.firstinspires.ftc.teamcode.akit.mechanism.LoggedMechanism2d
import org.firstinspires.ftc.teamcode.wpi.StructSerializable

interface LoggableName {
    infix fun value(value: StructSerializable)
    infix fun value(value: LoggedMechanism2d)
    infix fun value(value: Array<StructSerializable>)
}
fun Any.log(name: String) = object : LoggableName {
    override fun value(value: StructSerializable) {
        Logger.recordOutput(this::class.simpleName + "/" + name, value)
    }
    override fun value(value: LoggedMechanism2d) {
        Logger.recordOutput(this::class.simpleName + "/" + name, value)
    }
    override fun value(value: Array<StructSerializable>) {
        Logger.recordOutput(this::class.simpleName + "/" + name, value)
    }
}