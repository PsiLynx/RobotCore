package org.firstinspires.ftc.teamcode.command.internal

class WaitUntilCommand(until: () -> Boolean): Command(isFinished = until)