package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.log

@TeleOp(group = "a")
class FlywheelFullSend: CommandOpMode() {
    override fun postSelector() {
        Flywheel.run {
                it.setPower(1.0)
        }.schedule()

        Telemetry.addAll {
            "vel" ids { Flywheel.currentState.velocity }
        }
    }
}