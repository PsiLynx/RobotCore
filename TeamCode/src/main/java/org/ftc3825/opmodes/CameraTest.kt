package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.Telemetry


@TeleOp(name = "Red Blob Detection")
class CameraTest : CommandOpMode() {
    override fun init() {

        Extendo.justUpdate().schedule()

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("samples") { Extendo.samples }
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()
    }


}
