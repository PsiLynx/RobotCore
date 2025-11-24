package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain

@Autonomous(name = "Drive Forward", group = "a")
class DriveForwardAuto: CommandOpMode() {
    override fun postSelector() {
        Drivetrain.run { it.setWeightedDrivePower(drive = -0.25) }
            .schedule()
    }
}
