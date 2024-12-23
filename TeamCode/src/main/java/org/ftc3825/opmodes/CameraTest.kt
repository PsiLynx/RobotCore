package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Rotation2D


@TeleOp(name = "Red Blob Detection")
class CameraTest : CommandOpMode() {
    override fun init() {

        RunCommand(Extendo, Drivetrain) {
            Drivetrain.setWeightedDrivePower(
                ( Extendo.samples.minBy { it.magSq }.vector + Rotation2D() ) / 100.0
            )
        }

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("samples") { Extendo.samples }
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()
    }


}
