package org.firstinspires.ftc.teamcode.opmodes.dt

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain

@TeleOp(group = "a")
class TestLocalizer: CommandOpMode() {
    override fun postSelector() {
        Drivetrain.justUpdate().schedule()
    }
}