package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.constant
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.tangent
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.gvf.path
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import kotlin.math.PI

@TeleOp(name = "Curve", group = "a")
class Curve: CommandOpMode() {
    override fun initialize() {
        Drivetrain.reset()
        CommandScheduler.reset()
        val p2 = Pose2D(40, 40, 0)
        val forwardPath = followPath {
            start(0, 0)
            curveTo(
                p2.x, 0,
                0, p2.y,
                p2.x, p2.y,
                forward
            )
        }
        val backPath = followPath {
            start(p2.vector + Vector2D(0, 10))
            curveTo(
                0, -p2.y,
                -p2.x, 0,
                0, 0,
                forward
            )
        }

        Drivetrain.position = Pose2D(0, 0, PI / 2)

        val driver = Gamepad(gamepad1!!)

        driver.y.onTrue(forwardPath withEnd { Drivetrain.resetPoseHistory() })
        driver.a.onTrue(backPath withEnd { Drivetrain.resetPoseHistory() })
        Drivetrain.justUpdate().schedule()

        RunCommand { println(Drivetrain.position) }.schedule()
        Telemetry.update()

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "vel" ids Drivetrain::velocity
            "endVel" ids forwardPath.path.currentPath::endVelocity
            ""    ids CommandScheduler::status
        }
        Telemetry.justUpdate().schedule()
    }
}