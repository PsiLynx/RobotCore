package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter
import org.firstinspires.ftc.teamcode.subsystem.telemetry.Telemetry
import org.firstinspires.ftc.teamcode.util.log

@TeleOp(group = "a")
class FlywheelFullSend: CommandOpMode() {
    override fun postSelector() {
        Shooter.run {
                io.flywheelSetPower(1.0)
        }.schedule()

        Telemetry.addAll {
            "vel" ids { Shooter.io.flywheelCurrentState.velocity }
        }
    }
}