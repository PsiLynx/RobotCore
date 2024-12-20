package org.ftc3825.command

import org.ftc3825.command.internal.Command
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.Rotation2D
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.util.Vector2D
import java.util.function.DoubleSupplier
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
            val translational = if (drive.asDouble == 0.0 && strafe.asDouble == 0.0 && OuttakeSlides.position < 450) {
                Vector2D(xVelocityController.feedback, yVelocityController.feedback)
            } else (Vector2D(drive.asDouble, strafe.asDouble) rotatedBy position.heading)

            if (abs(robotCentricVelocity.heading.toDouble()) < 0.01) holdingHeading = true

            val rotational = if (turn.asDouble == 0.0 && !holdingHeading) {
                targetHeading = position.heading
                Rotation2D(headingVelocityController.feedback)
            } else if (turn.asDouble == 0.0) {
                Rotation2D(headingController.feedback)
            } else {
                holdingHeading = false
                Rotation2D(turn.asDouble)
            }

            setWeightedDrivePower(translational + rotational)
        }
    }
}