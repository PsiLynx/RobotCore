package org.teamcode.command.internal

class WaitUntilCommand(until: () -> Boolean): Command(isFinished = until)