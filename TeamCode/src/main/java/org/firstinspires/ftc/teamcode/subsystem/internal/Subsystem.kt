package org.firstinspires.ftc.teamcode.subsystem.internal

import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.util.Globals

abstract class Subsystem<T : Subsystem<T,*>, IoT : SubsystemIo >(
    realio: IoT,
    fakeio: IoT,
){

    open val io: IoT = if (Globals.fake) fakeio else realio

    open fun update(deltaTime: Double = 0.0) { }

    open fun reset() { }

    fun run(function: T.(T) -> Unit)
        = RunCommand(this) { (this as T).function(this as T) }

    fun runOnce(function: T.(T) -> Unit)
        = InstantCommand(this) { (this as T).function(this as T) }

    fun justUpdate() = (
        run {}
        withName "justUpdate"
        withDescription { (this as T)::class.simpleName!! }
    )

}