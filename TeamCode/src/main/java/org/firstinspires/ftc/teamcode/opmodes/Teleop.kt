package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.TeleopDrivePowers
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.Cameras
import org.firstinspires.ftc.teamcode.component.Component.Opening.CLOSED
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Intake
import org.firstinspires.ftc.teamcode.subsystem.LEDs
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI

@TeleOp(name = "ROBOT CENTRIC")
class Teleop: CommandOpMode() {
    override fun postSelector() {

        // Set position
        //Drivetrain.position = Pose2D(-72 + 7.75 + 8, 72 - 22.5 - 7, -PI/2)

        InstantCommand {
            println("all hubs: ")
            println(this.allHubs.joinToString())
            println("^^^")
        }.schedule()
        Cameras.justUpdate().schedule()

        val dtControl = TeleopDrivePowers(driver, operator)
        dtControl.schedule()

        driver.apply {
            leftBumper.onTrue(Intake.run())
            leftTrigger.onTrue(Intake.stop())

            rightBumper.onTrue(CyclicalCommand(
                Flywheel.stop(),

                ShootingStateOTM()
            ).nextCommand())

            rightTrigger.whileTrue(
                Robot.kickBalls()
            )

            x.whileTrue(Intake.run())
            y.whileTrue(TankDrivetrain.readAprilTags())
            b.onTrue(
                InstantCommand {
                    TankDrivetrain.position = Pose2D(
                        0, -72 + 7, PI/2
                    )
                }
            )

        }
        RunCommand {
            log("alliance") value Globals.alliance.toString()
        }


        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "" ids CommandScheduler::status
        }
    }
}
