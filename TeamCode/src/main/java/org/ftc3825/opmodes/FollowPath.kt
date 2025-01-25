package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.gvf.HeadingType
import org.ftc3825.gvf.path
import org.ftc3825.util.Drawing
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.geometry.Rotation2D
import kotlin.math.PI

@Autonomous(name = "FollowPath", group = "a")
class FollowPath: CommandOpMode() {


    override fun init() {
        initialize()
        Drivetrain.reset()
        var goingForward = true
        val forward = path {
            start(0, 0)
            lineTo(0, 40, HeadingType.constant(PI / 2))
        }
        val back = path {
            start(0, 40)
            lineTo(0, 0, HeadingType.constant(PI / 2))
        }
        val forwardCommand = FollowPathCommand(forward)
        val backCommand = FollowPathCommand(back)
        fun power() = if(goingForward) forwardCommand.power
                      else backCommand.power

        Drivetrain.position = Pose2D(0, 0, PI / 2)

        /*RepeatCommand(
            times   = 10,
            command = */(
            (
                forwardCommand
//                andThen InstantCommand { goingForward = false }
//                andThen back
//                andThen InstantCommand { goingForward = true }
            )
        ).schedule()

        RunCommand { Drawing.sendPacket() }.schedule()
        RunCommand { println(Drivetrain.position.vector) }.schedule()
        Drawing.sendPacket()
        Telemetry.update()

        Telemetry.addAll {
            "pos"     ids Drivetrain::position
            "forward" ids { goingForward }
            "power"   ids ::power
            "error"   ids { forward[-1].getRotationalError(Rotation2D(), 1.0)}
            ""        ids CommandScheduler::status
        }
    }
}