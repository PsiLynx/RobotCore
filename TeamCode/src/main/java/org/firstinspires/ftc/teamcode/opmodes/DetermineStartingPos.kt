package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import kotlin.math.PI

@TeleOp(group = "a")
class DetermineStartingPos: CommandOpMode() {
    override fun postSelector() {
        TankDrivetrain.reset()

        TankDrivetrain.position = Pose2D(
            0, 0, PI /2
        )
        TankDrivetrain.justUpdate().schedule()
        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
        }
    }
}