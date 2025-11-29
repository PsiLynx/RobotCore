package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import java.util.function.BooleanSupplier
import java.util.function.DoubleSupplier

class TeleopDrivePowers(
    val driver: Gamepad,
    val operator: Gamepad,
): Command() {
    override val requirements = mutableSetOf<Subsystem<*>>(Drivetrain)

    var strafeAllowed = true
    override fun initialize() { strafeAllowed = true }

    override fun isFinished() = false
    override fun execute() = with(Drivetrain) {
        if(operator.a.supplier.asBoolean) strafeAllowed = true
        if(operator.b.supplier.asBoolean) strafeAllowed = false

        val drive  = - driver.leftStick.y.sq
        val turn   = - driver.rightStick.x.sq

        val strafe =   if(strafeAllowed) driver.leftStick.x.sq else 0.0

        val slowMode    = driver.b.supplier.asBoolean
        val lockHeading = driver.a.supplier.asBoolean

        headingController.targetPosition = shootingTargetHead.toDouble()

        val rotational = (
            if (lockHeading) headingController.feedback
            else turn
        ) * (if(slowMode) 0.5 else 1.0)

        setWeightedDrivePower(
            drive  = drive  * if(slowMode) 0.5 else 1.0,
            strafe = strafe * if(slowMode) 0.5 else 1.0,
            turn = rotational.toDouble()
        )
    }
}