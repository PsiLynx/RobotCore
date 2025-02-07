package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.geometry.Vector2D


@TeleOp(name = "Camera test")
class CameraTest : CommandOpMode() {
    override fun init() {
        initialize()
        Extendo.reset()
        Telemetry.reset()

        Extendo.justUpdate().schedule()

        Telemetry.addFunction("samples") { Extendo.samples }
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()
    }


}
