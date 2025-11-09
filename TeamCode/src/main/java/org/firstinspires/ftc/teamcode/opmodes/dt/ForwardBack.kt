package org.firstinspires.ftc.teamcode.opmodes.dt

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import kotlin.math.PI

@TeleOp(name = "forward / back", group = "a")
class ForwardBack: CommandOpMode() {
    override fun postSelector() {
        Drivetrain.reset()
        CommandScheduler.reset()
        val forward = followPath {
            start(0, 0)
            lineTo(0, 40, HeadingType.Companion.forward)
        }
        val back = followPath {
            start(0, 40)
            lineTo(0, 0, HeadingType.Companion.forward)
        }

        Drivetrain.position = Pose2D(0, 0, PI / 2)

        val driver = Gamepad(gamepad1!!)

        driver.y.onTrue(forward)
        driver.a.onTrue(back)
        Drivetrain.justUpdate().schedule()

        RunCommand { println(Drivetrain.position) }.schedule()
        RunCommand { Thread.sleep(6L) }.schedule()
        Telemetry.update()

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "y"   ids { Drivetrain.position.y }
            "x"   ids { Drivetrain.position.x }
            "h"   ids { Drivetrain.position.heading }
            ""    ids CommandScheduler::status
        }
        Telemetry.justUpdate().schedule()
        println("initialized")
    }
}