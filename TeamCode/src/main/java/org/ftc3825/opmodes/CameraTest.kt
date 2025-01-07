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
        Drivetrain.reset()
        Telemetry.reset()

        RunCommand(Extendo, Drivetrain) {
            val vector = (
                Extendo.samples.minByOrNull { it.magSq }?.vector
                ?: Vector2D()
            ) / 200.0
            Drivetrain.setWeightedDrivePower(
                vector.y,
                -vector.x,
                -(Drivetrain.position.heading.toDouble() / 10)
            )
        }.schedule()

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("samples") { Extendo.samples }
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()
    }


}
