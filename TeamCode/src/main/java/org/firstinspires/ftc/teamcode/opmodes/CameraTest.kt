package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.subsystem.Extendo
import org.firstinspires.ftc.teamcode.subsystem.Telemetry


@TeleOp(name = "Camera test")
@Disabled
class CameraTest : CommandOpMode() {
    override fun initialize() {
        Extendo.reset()
        Telemetry.reset()

        Extendo.justUpdate().schedule()

        Telemetry.addFunction("samples") { Extendo.samples }
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()
    }


}
