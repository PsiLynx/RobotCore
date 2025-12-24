package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import java.util.function.BooleanSupplier
import java.util.function.DoubleSupplier

class TeleopDrivePowers(
    val driver: Gamepad,
    val operator: Gamepad,
): Command() {
    override val requirements = mutableSetOf<Subsystem<*>>(TankDrivetrain)

    override fun isFinished() = false
    override fun execute() = with(TankDrivetrain) {

        val drive  = - driver.leftStick.y.sq
        val turn   = - driver.rightStick.x.sq

        val slowMode    = driver.leftStick.supplier.asBoolean

        val rotational = turn * (if(slowMode) 0.5 else 1.0)

        setWeightedDrivePower(
            drive  = drive  * if(slowMode) 0.5 else 1.0,
            turn = rotational.toDouble()
        )
    }
}