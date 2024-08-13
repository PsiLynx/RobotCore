package org.ftc3825.command.internal

class WaitUntilCommand(until: () -> Boolean): Command(isFinished = until)