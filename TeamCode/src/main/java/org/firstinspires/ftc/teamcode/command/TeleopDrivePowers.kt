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
    val headingVec: Supplier<Vector2D>
): Command() {
    override fun isFinished() = false
    override val requirements = mutableSetOf<Subsystem<*>>(Drivetrain)
    override fun execute() {
        with(Drivetrain) {
            val translational = (
                Vector2D(drive.asDouble, -strafe.asDouble) * 1.0
                rotatedBy ( -position.heading - Rotation2D(PI) )
            )
            //if(translational.mag < 0.1) { translational = Vector2D() }

            val rotational = headingController.feedback

            setWeightedDrivePower(
                drive = translational.y,
                strafe = translational.x,
                turn = rotational.toDouble()
            )
        }
    }
}