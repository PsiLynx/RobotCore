package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import java.util.function.BooleanSupplier
import java.util.function.DoubleSupplier

class TeleopDrivePowers(
    val drive: DoubleSupplier,
    val strafe: DoubleSupplier,
    val joyStick: DoubleSupplier,
    val lockHeading: BooleanSupplier
): Command() {
    override val requirements = mutableSetOf<Subsystem<*>>(Drivetrain)

    override fun isFinished() = false
    override fun execute() = with(Drivetrain) {
        headingController.targetPosition = shootingTargetHead.toDouble()

        var translational = Vector2D(strafe.asDouble, drive.asDouble)
        if (translational.mag < 0.1) {
            translational = Vector2D()
        }

        val rotational = (
            if (lockHeading.asBoolean) headingController.feedback
            else joyStick.asDouble
        )

        setWeightedDrivePower(
            drive = translational.y,
            strafe = translational.x,
            turn = rotational.toDouble()
        )
    }
}