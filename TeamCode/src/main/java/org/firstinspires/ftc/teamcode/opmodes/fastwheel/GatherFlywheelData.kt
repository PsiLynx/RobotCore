package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.intake.Intake
import org.firstinspires.ftc.teamcode.subsystem.robot.Robot
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter
import org.firstinspires.ftc.teamcode.subsystem.telemetry.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.degrees

@TeleOp(group = "a")
class GatherFlywheelData: CommandOpMode() {
    override fun postSelector() {
        Shooter.run { io.flywheelUsingFeedback = true }.schedule()
        driver.rightTrigger.whileTrue(Robot.kickBalls())
        driver.leftBumper.whileTrue(Intake.run())

        driver.run {
            dpadLeft.onTrue(InstantCommand {
                Shooter.io.flywheelTargetState -= VaState(20.0, 0.0)
            })

            dpadRight.onTrue(InstantCommand {
                Shooter.io.flywheelTargetState += VaState(20.0, 0.0)
            })

            a.onTrue(InstantCommand{
                Shooter.io.hoodTargetAngle -= degrees(5)
            })

            y.onTrue(InstantCommand{
                Shooter.io.hoodTargetAngle += degrees(5)
            })
        }

        Telemetry.addAll {
            "vel" ids { Shooter.io.flywheelCurrentState.velocity.toDouble() }
            "theta" ids Shooter.io::hoodTargetAngle
            "t" ids Globals::currentTime
        }
    }

}