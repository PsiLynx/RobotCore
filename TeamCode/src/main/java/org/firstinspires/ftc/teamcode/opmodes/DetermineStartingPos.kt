package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import kotlin.math.PI

@TeleOp
class DetermineStartingPos: CommandOpMode() {
    override fun postSelector() {
        Drivetrain.reset()

        Drivetrain.position = Pose2D(
            -8.25, 7, PI /2
        )
        Drivetrain.justUpdate().schedule()
        Telemetry.addAll {
            "pos" ids Drivetrain::position
        }
    }
}