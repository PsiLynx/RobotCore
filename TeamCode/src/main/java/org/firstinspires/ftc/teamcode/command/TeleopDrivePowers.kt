package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import java.util.function.DoubleSupplier
import java.util.function.Supplier
import kotlin.math.PI

class TeleopDrivePowers(
    val drive: DoubleSupplier,
    val strafe: DoubleSupplier,
    val headingPow: DoubleSupplier,
): Command() {
    var lockHeading = false

    override fun isFinished() = false
    override val requirements = mutableSetOf<Subsystem<*>>(Drivetrain)
    override fun execute() {
        with(Drivetrain) {
            headingController.targetPosition = 45.0

            val translational = Vector2D(
                strafe.asDouble, drive.asDouble
            )
            //if(translational.mag < 0.1) { translational = Vector2D() }

            val rotational = (
                if(lockHeading) headingController.feedback
                else headingPow.asDouble
            )

            setWeightedDrivePower(
                drive = translational.y,
                strafe = translational.x,
                turn = rotational.toDouble()
            )
        }
    }
}