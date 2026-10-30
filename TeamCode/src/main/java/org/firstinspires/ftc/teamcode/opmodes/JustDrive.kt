package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.TeleopDrivePowers
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain

@TeleOp(group = "a")
class JustDrive: CommandOpMode() {
    override fun preSelector() {
        Drivetrain
    }

    override fun postSelector() {
        TeleopDrivePowers(driver, operator).schedule()
    }
}
