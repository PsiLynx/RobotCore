package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import java.util.function.DoubleSupplier

class TeleopDrivePowers(
    val drive: DoubleSupplier,
    val strafe: DoubleSupplier,
    val leftTrigger: DoubleSupplier,
    val rightTrigger: DoubleSupplier,
): Command() {
    override val requirements = mutableSetOf<Subsystem<*>>(Drivetrain)

    var lockHeading = false

    override fun isFinished() = false
    override fun execute() = with(Drivetrain) {
        headingController.targetPosition = shootingTargetHead.toDouble()

        var translational = Vector2D(strafe.asDouble, drive.asDouble)
        if (translational.mag < 0.1) {
            translational = Vector2D()
        }

        lockHeading =
            (leftTrigger.asDouble - rightTrigger.asDouble) < 0.3
            && leftTrigger.asDouble > 0.5
            && rightTrigger.asDouble > 0.5

        val rotational = (
            if (lockHeading) headingController.feedback
            else leftTrigger.asDouble - rightTrigger.asDouble
        )

        setWeightedDrivePower(
            drive = translational.y,
            strafe = translational.x,
            turn = rotational.toDouble()
        )
    }
}