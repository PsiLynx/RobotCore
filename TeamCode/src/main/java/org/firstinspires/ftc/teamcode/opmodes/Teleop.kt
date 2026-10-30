package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.TeleopDrivePowers
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.DeferredCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.If
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.component.Component.Opening.CLOSED
import org.firstinspires.ftc.teamcode.component.Component.Opening.OPEN
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.subsystem.intake.Intake
import org.firstinspires.ftc.teamcode.subsystem.telemetry.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.subsystem.robot.Robot
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.intake.Io
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI

@TeleOp(name = "ROBOT CENTRIC")
class Teleop: CommandOpMode() {
    override fun postSelector() {

        // Set position
        //Drivetrain.io.position = Pose2D(-72 + 7.75 + 8, 72 - 22.5 - 7, -PI/2)

        // Cameras.justUpdate().schedule()
        val dtControl = TeleopDrivePowers(driver, operator)
        dtControl.schedule()

        //Robot.RightTriggerManager(driver.rightTrigger).schedule()

        var targetFlywheelPower = 0.5
        val flywheelCycle = CyclicalCommand(
            Shooter.run { it.io.flywheelSetPower(0.0) },

            DeferredCommand {
                Shooter.run { it.io.flywheelSetPower(targetFlywheelPower) }
            }
        )
        val flowerCycle = CyclicalCommand(
            InstantCommand { Intake.io.setFlowerPos(Io.FlowerPosition.DOWN) },
            InstantCommand { Intake.io.setFlowerPos(Io.FlowerPosition.UP) },
        )
        Shooter.justUpdate().schedule()
        driver.apply {

            leftBumper.whileTrue(Intake.run())

            leftTrigger.whileTrue(Intake.backOut())

            rightBumper.onTrue(flywheelCycle.nextCommand())
            rightTrigger.whileTrue(Robot.kickBalls())

            x.whileTrue(Intake.run(motorPow = -1.0))
            y.onTrue(InstantCommand {
                if(Shooter.io.positionEstimate.age() < 0.5)
                Drivetrain.io.position = Shooter.io.positionEstimate.value
            })

            b.onTrue(
                InstantCommand {
                    Drivetrain.io.position = Pose2D(
                        72 - 8, 72 - 8, PI / 2
                    )
                }
            )
            a.onTrue(flowerCycle.nextCommand())

            dpadUp.onTrue(InstantCommand { targetFlywheelPower += 0.05 })
            dpadUp.onTrue(InstantCommand { targetFlywheelPower -= 0.05 })


        }
        operator.apply {
            x.onTrue(InstantCommand {
                Drivetrain.io.resetLocalizer()
                Drivetrain.io.position = Pose2D(0, 0, PI / 2)
            })
        }

        RunCommand {
            log("alliance") value Globals.alliance.toString()
        }


        Telemetry.addAll {
            "pos" ids Drivetrain.io::position
            "alliance" ids Globals::alliance
            "" ids CommandScheduler::status
        }
    }
}
