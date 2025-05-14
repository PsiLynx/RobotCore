package org.firstinspires.ftc.teamcode.command.internal.controlFlow

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandGroup
import kotlin.Int

class For(
    start: Double = 0.0,
    step: Double = 1.0,
    stop: Double,
    command: (Double) -> Command
): CommandGroup(
    *Array(
        ((stop - start ) / step).toInt()
    ) { i -> command( i * step + start)}
){
    constructor(
        start: Double = 0.0,
        step: Double = 1.0,
        stop: Double,
        command: Command
    ): this(start, step, stop, { command })
}