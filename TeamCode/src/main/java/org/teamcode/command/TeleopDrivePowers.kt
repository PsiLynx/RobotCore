package org.teamcode.command

import org.teamcode.command.internal.Command
import org.teamcode.subsystem.Drivetrain
import org.teamcode.subsystem.Subsystem
import org.teamcode.util.geometry.Rotation2D
import org.teamcode.util.geometry.Vector2D
import java.util.function.DoubleSupplier
import kotlin.math.PI
import kotlin.math.abs

class TeleopDrivePowers(
    val drive: DoubleSupplier,
    val strafe: DoubleSupplier,
    val turn: DoubleSupplier
): Command() {
    override fun isFinished() = false
    override val requirements = mutableSetOf<Subsystem<*>>(Drivetrain)
    override fun execute() {
        with(Drivetrain) {
            var translational = (
                Vector2D(drive.asDouble, -strafe.asDouble)
                rotatedBy ( -position.heading - Rotation2D(PI) )
            )
            //if(translational.mag < 0.1) { translational = Vector2D() }

            if (
                abs(robotCentricVelocity.heading.toDouble()) < 0.01
            ) holdingHeading = true

            val rotational = if (turn.asDouble == 0.0 && !holdingHeading) {
                targetHeading = position.heading
                Rotation2D(headingVelocityController.feedback)
            } else if (turn.asDouble == 0.0) {
                Rotation2D(headingController.feedback)
            } else {
                holdingHeading = false
              Rotation2D(turn.asDouble)
            }

            setWeightedDrivePower(
                drive = translational.y,
                strafe = translational.x,
                turn = rotational.toDouble()
            )
        }
    }
}