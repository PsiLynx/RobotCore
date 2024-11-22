package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.Pose2D

@Autonomous(name = "Drive Forward", group = "a")
class DriveForwardAuto: CommandOpMode() {

    override fun init() {
        initialize()

        Drivetrain.run {
            it.setWeightedDrivePower(Pose2D(
                  -0.25, 0.0, 0.0
            ))
        }.schedule()
    }
}
