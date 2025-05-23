package org.firstinspires.ftc.teamcode.component.controller

import org.firstinspires.ftc.teamcode.command.internal.Trigger
import java.util.function.BooleanSupplier
import java.util.function.DoubleSupplier

class GamepadTrigger(
    private val doubleSupplier: DoubleSupplier,
    var threshold: Double
): Trigger( { doubleSupplier.asDouble > threshold } ) {
    constructor(doubleSupplier: () -> Double) : this(doubleSupplier, 0.7)

    fun toDouble() = doubleSupplier.asDouble
}