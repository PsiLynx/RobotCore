package org.ftc3825.opmodes

import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RepeatCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.gvf.HeadingType
import org.ftc3825.gvf.followPath
import org.ftc3825.gvf.path
import org.ftc3825.pedroPathing.util.Drawing
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Pose2D
import kotlin.math.PI

class FollowPath: CommandOpMode() {
    var goingForward = true
    val forward = followPath {
        start(0, 0)
        lineTo(10, 0, HeadingType.Constant(PI / 2))
    }
    val back = followPath {
        start(10, 0)
        lineTo(0, 0, HeadingType.Constant(PI / 2))
    }
    val power: Pose2D
        get() = if(goingForward) forward.power
                else back.power
    override fun init() {
        initialize()
        Drivetrain.reset()
        Telemetry.reset()

        Drivetrain.position = Pose2D(0, 0, PI / 2)

        RepeatCommand(
            times   = 10,
            command = (
                forward
                andThen InstantCommand { goingForward = false }
                andThen back
                andThen InstantCommand { goingForward = true }
            )
        ).schedule()

        RunCommand { Drawing.sendPacket() }.schedule()

        Telemetry.telemetry = telemetry
        Telemetry.justUpdate().schedule()

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "power" ids { power }
        }
    }
}