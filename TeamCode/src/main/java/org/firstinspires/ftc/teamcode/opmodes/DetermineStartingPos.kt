package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.telemetry.Telemetry
import kotlin.math.PI

@TeleOp(group = "a")
class DetermineStartingPos: CommandOpMode() {
    override fun postSelector() {
        Drivetrain.reset()

        Drivetrain.io.position = Pose2D(
            9.6/2, 0, PI / 2
        )
        Drivetrain.justUpdate().schedule()
        Telemetry.addAll {
            "pos" ids Drivetrain.io::position
        }
    }
}