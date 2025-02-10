package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.component.Gamepad
import org.ftc3825.gvf.HeadingType
import org.ftc3825.gvf.followPath
import org.ftc3825.gvf.path
import org.ftc3825.util.Drawing
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.geometry.Rotation2D
import kotlin.math.PI

@TeleOp(name = "FollowPath", group = "a")
class FollowPath: CommandOpMode() {
    override fun init() {
        initialize()
        Drivetrain.reset()
        CommandScheduler.reset()
        val forward = followPath {
            start(0, 0)
            lineTo(0, 40, HeadingType.constant(PI / 2))
        }
        val back = followPath {
            start(0, 40)
            lineTo(0, 0, HeadingType.constant(PI / 2))
        }

        Drivetrain.position = Pose2D(0, 0, PI / 2)

        val driver = Gamepad(gamepad1!!)

        driver.y.onTrue(forward)
        driver.a.onTrue(back)

        RunCommand { Drawing.sendPacket() }.schedule()
        RunCommand { println(Drivetrain.position.vector) }.schedule()
        Drawing.sendPacket()
        Telemetry.update()

        Telemetry.addAll {
            "pos"     ids Drivetrain::position
            ""        ids CommandScheduler::status
        }
        Telemetry.justUpdate().schedule()
    }
}