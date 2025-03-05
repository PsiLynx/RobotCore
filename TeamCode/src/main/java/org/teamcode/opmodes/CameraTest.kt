package org.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.teamcode.command.internal.CommandScheduler
import org.teamcode.subsystem.Extendo
import org.teamcode.subsystem.Telemetry


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
