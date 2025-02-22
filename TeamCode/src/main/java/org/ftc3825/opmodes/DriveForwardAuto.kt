package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.subsystem.Drivetrain

@Autonomous(name = "Drive Forward", group = "a")
class DriveForwardAuto: CommandOpMode() {
    override fun initialize() {
        Drivetrain.run { it.setWeightedDrivePower(drive = -0.25) }
            .schedule()
    }
}
