package org.firstinspires.ftc.teamcode.command.internal

import java.util.function.BooleanSupplier

class WaitUntilCommand(until: () -> Boolean): Command(isFinished = until)