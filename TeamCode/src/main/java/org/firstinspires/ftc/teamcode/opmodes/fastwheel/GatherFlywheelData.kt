package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.flywheel.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.hood.Hood
import org.firstinspires.ftc.teamcode.subsystem.intake.Intake
import org.firstinspires.ftc.teamcode.subsystem.robot.Robot
import org.firstinspires.ftc.teamcode.subsystem.telemetry.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.degrees

@TeleOp(group = "a")
class GatherFlywheelData: CommandOpMode() {
    override fun postSelector() {
        Flywheel.run { it.usingFeedback = true }.schedule()
        Hood.justUpdate().schedule()
        driver.rightTrigger.whileTrue(Robot.kickBalls())
        driver.leftBumper.whileTrue(Intake.run())

        driver.run {
            dpadLeft.onTrue(InstantCommand {
                Flywheel.targetState -= VaState(20.0, 0.0)
            })

            dpadRight.onTrue(InstantCommand {
                Flywheel.targetState += VaState(20.0, 0.0)
            })

            a.onTrue(InstantCommand{
                Hood.targetAngle -= degrees(5)
            })

            y.onTrue(InstantCommand{
                Hood.targetAngle += degrees(5)
            })
        }

        Telemetry.addAll {
            "vel" ids { Flywheel.currentState.velocity.toDouble() }
            "theta" ids Hood::targetAngle
            "t" ids Globals::currentTime
        }
    }

}