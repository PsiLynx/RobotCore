package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.geometry.DrivePowers
import org.ftc3825.util.geometry.Pose2D

@Autonomous(name = "Drive Forward", group = "a")
class DriveForwardAuto: CommandOpMode() {
    override fun initialize() {
        Drivetrain.run {
            it.setWeightedDrivePower(
                DrivePowers(
                  -0.25, 0.0, 0.0
                )
            )
        }.schedule()
    }
}
