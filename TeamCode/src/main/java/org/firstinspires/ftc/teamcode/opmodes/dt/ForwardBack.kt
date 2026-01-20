package org.firstinspires.ftc.teamcode.opmodes.dt

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Intake
import kotlin.math.PI

@TeleOp(name = "forward / back", group = "a")
class ForwardBack: CommandOpMode() {
    override fun postSelector() {
        TankDrivetrain.reset()
        CommandScheduler.reset()
        val forward = followPath {
            start(0, 0)
            lineTo(0, 60, HeadingType.Companion.tangent)
        }
        val back = followPath {
            start(0, 60)
            lineTo(0, 0, HeadingType.Companion.reverseTangent)
        }

        TankDrivetrain.position = Pose2D(0, 0, PI / 2)

        val driver = Gamepad(gamepad1!!)

        driver.y.onTrue(forward)
        driver.a.onTrue(back)
        driver.rightTrigger.onTrue(CyclicalCommand(
            Flywheel.stop() parallelTo Intake.stop(),
            Flywheel.fullSend() parallelTo Intake.run()
        ).nextCommand())

        TankDrivetrain.justUpdate().schedule()

        RunCommand { println(TankDrivetrain.position) }.schedule()
        RunCommand { Thread.sleep(6L) }.schedule()
        Telemetry.update()

        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "y"   ids { TankDrivetrain.position.y }
            "x"   ids { TankDrivetrain.position.x }
            "h"   ids { TankDrivetrain.position.heading }
            ""    ids CommandScheduler::status
        }
        Telemetry.justUpdate().schedule()
        println("initialized")
    }
}