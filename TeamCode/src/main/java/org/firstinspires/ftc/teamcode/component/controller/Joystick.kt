package org.firstinspires.ftc.teamcode.component.controller

import org.firstinspires.ftc.teamcode.command.internal.Trigger
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import java.util.function.DoubleSupplier

data class Joystick(
    val x: GamepadAxis,
    val y: GamepadAxis,
    val button: Trigger
): Trigger( button.supplier ){

    val theta: Rotation2D
	get() = Vector2D(x.toDouble(), -y.toDouble()).theta
    val mag: Double
	get() = Vector2D(x.toDouble(), -y.toDouble()).mag
}
