package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import kotlin.math.PI

@TeleOp(name = "FollowPath", group = "a")
@Disabled
class FollowPath: CommandOpMode() {
    override fun initialize() {
        Drivetrain.reset()
        CommandScheduler.reset()
        val forward = followPath {
            // comment
            start(0, 0) // another comment
            lineTo(0, 20, HeadingType.constant(PI / 2))
        }
        val back = followPath {
            start(0, 20)
            lineTo(0, 0, HeadingType.constant(PI / 2))
        }

        Drivetrain.position = Pose2D(0, 0, PI / 2)

        val driver = Gamepad(gamepad1!!)

        driver.y.onTrue(forward)
        driver.a.onTrue(back)

        RunCommand { println(Drivetrain.position.vector) }.schedule()
        Telemetry.update()

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "y"   ids { Drivetrain.position.y }
            "x"   ids { Drivetrain.position.x }
            "h"   ids { Drivetrain.position.heading }
            ""    ids CommandScheduler::status
        }
        Telemetry.justUpdate().schedule()
    }
}