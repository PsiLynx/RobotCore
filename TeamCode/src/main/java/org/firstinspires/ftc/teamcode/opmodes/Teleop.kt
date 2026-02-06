package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.TeleopDrivePowers
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.If
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.component.Component.Opening.CLOSED
import org.firstinspires.ftc.teamcode.component.Component.Opening.OPEN
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.shooter.ShooterConfig
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Intake
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
        //TankDrivetrain.position = Pose2D(-72 + 7.75 + 8, 72 - 22.5 - 7, -PI/2)

        // Cameras.justUpdate().schedule()
        TankDrivetrain.motors.forEach {
            it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
        }

        val dtControl = TeleopDrivePowers(driver, operator)
        dtControl.schedule()

        //Robot.RightTriggerManager(driver.rightTrigger).schedule()

        val flywheelCycle = CyclicalCommand(
            Flywheel.stop(),

            ShootingStateOTM()
        )
        driver.apply {
            b.whileTrue(TankDrivetrain.run {
                    it.motors.forEach {
                        it.setZeroPowerBehavior(Motor.ZeroPower.FLOAT)
                        it.power = 0.0
                    }
                }
            ).onFalse(
                TankDrivetrain.runOnce {
                    it.motors.forEach {
                        it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
                    }
                } andThen dtControl
            )

            leftBumper.whileTrue(Intake.run(transferSpeed = 0.1))
            leftTrigger.whileTrue(
                (
                    RunCommand(Flywheel) {
                        Flywheel.targetState = VaState(100.0, 0.0)
                        Flywheel.usingFeedback = true
                    } parallelTo Intake.run(
                        blockerPos = OPEN,
                        propellerPos = CLOSED,
                        transferSpeed = 0.3,
                        motorPow = 1.0
                    )
                )
            ).onFalse(flywheelCycle.current)

            rightBumper.onTrue(flywheelCycle.nextCommand())
            rightTrigger.whileTrue(Robot.kickBalls())

            x.whileTrue(
                Intake.run(
                    propellerPos = CLOSED,
                    blockerPos = OPEN,
                    motorPow = -1.0,
                    transferSpeed = -1.0,
                )
            )
            y.whileTrue(TankDrivetrain.readAprilTags())
            b.onTrue(
                InstantCommand {
                    TankDrivetrain.position = Pose2D(
                        0, -72 + 7, PI / 2
                    )
                }
            )

        }
        operator.apply {
            x.onTrue(InstantCommand {
                TankDrivetrain.resetLocalizer()
                TankDrivetrain.position = Pose2D(0, 0, PI / 2)
            })
            dpadUp.onTrue(InstantCommand {
                ShooterConfig.redGoalY ++
            })
            dpadDown.onTrue(InstantCommand {
                ShooterConfig.redGoalY --
            })
            dpadLeft.onTrue(InstantCommand {
                if(Globals.alliance == Globals.Alliance.BLUE) {
                    ShooterConfig.redGoalX ++
                } else {
                    ShooterConfig.redGoalX --
                }
            })
            dpadRight.onTrue(InstantCommand {
                if(Globals.alliance == Globals.Alliance.BLUE) {
                    ShooterConfig.redGoalX --
                } else {
                    ShooterConfig.redGoalX ++
                }
            })
        }

        RunCommand {
            log("alliance") value Globals.alliance.toString()
        }


        Telemetry.addAll {
            "pos" ids TankDrivetrain::position
            "alliance" ids Globals::alliance
            "red goal" ids ShooterConfig::redGoal
            "" ids CommandScheduler::status
        }
    }
}
