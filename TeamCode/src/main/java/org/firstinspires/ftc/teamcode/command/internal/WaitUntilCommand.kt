package org.firstinspires.ftc.teamcode.command.internal

class WaitUntilCommand(until: () -> Boolean): Command(
    isFinished = until,
    name = { "Wait Until" },
    description = { "" }
)