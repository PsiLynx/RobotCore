package org.ftc3825.command.internal


class RepeatCommand(command: Command, times: Int):
    CommandGroup(*Array(times) { _ -> command.copy()})