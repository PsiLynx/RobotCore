package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain

@Autonomous(name = "Drive Forward", group = "a")
class DriveForwardAuto: CommandOpMode() {
    override fun postSelector() {
        TankDrivetrain.run { it.setWeightedDrivePower(drive = 0.25) }
            .schedule()
    }
}
