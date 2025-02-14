package org.ftc3825.command

import org.ftc3825.command.internal.Command
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.geometry.Rotation2D
import org.ftc3825.util.geometry.Vector2D
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