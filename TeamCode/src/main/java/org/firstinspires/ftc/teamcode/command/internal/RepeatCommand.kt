package org.firstinspires.ftc.teamcode.command.internal


class RepeatCommand(command: Command, times: Int):
    CommandGroup(*Array(times) { _ -> command.copy()})