package org.firstinspires.ftc.teamcode.component.controller

import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D

data class Joystick(val x: GamepadAxis, val y: GamepadAxis){
    val theta: Rotation2D
	get() = Vector2D(x.toDouble(), -y.toDouble()).theta
    val mag: Double
	get() = Vector2D(x.toDouble(), -y.toDouble()).mag
}
