package org.firstinspires.ftc.teamcode.component.controller

import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import java.util.function.DoubleSupplier

data class Joystick(val x: GamepadAxis, val y: GamepadAxis)