package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.subsystem.V2Claw


@TeleOp(name = "Red Blob Detection")
class CameraTest : CommandOpMode() {
    override fun init() {

        V2Claw.justUpdate().schedule()

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("samples") { V2Claw.getSamples() }
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()
    }


}
