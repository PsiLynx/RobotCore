package org.firstinspires.ftc.teamcode.command.internal.controlFlow

import org.firstinspires.ftc.teamcode.command.internal.Command

class Repeat(times: Int, command: (Int) -> Command): For(
    stop = times.toDouble(),
    command = {(
        command(it.toInt())
        withName { "rep ${it.toInt()}" }
        withDescription { command(it.toInt()).toString() }
    )},

)