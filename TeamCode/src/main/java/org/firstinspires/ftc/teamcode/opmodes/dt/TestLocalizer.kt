package org.firstinspires.ftc.teamcode.opmodes.dt

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain

@TeleOp
class TestLocalizer: CommandOpMode() {
    override fun postSelector() {
        TankDrivetrain.justUpdate().schedule()
    }
}