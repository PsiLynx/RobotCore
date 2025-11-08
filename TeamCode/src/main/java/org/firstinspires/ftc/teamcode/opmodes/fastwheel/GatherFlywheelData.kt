package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Hood
import org.firstinspires.ftc.teamcode.subsystem.Telemetry
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.degrees

@TeleOp
class GatherFlywheelData: CommandOpMode() {
    override fun initialize() {
        Flywheel.run { it.usingFeedback = true }.schedule()
        Hood.justUpdate().schedule()

        driver.run {
            dpadLeft.onTrue(InstantCommand {
                Flywheel.targetVelocity -= 0.05
            })

            dpadRight.onTrue(InstantCommand {
                Flywheel.targetVelocity += 0.05
            })

            a.onTrue(InstantCommand{
                Hood.targetAngle -= degrees(5)
            })

            y.onTrue(InstantCommand{
                Hood.targetAngle += degrees(5)
            })
        }

        Telemetry.addAll {
            "vel" ids Flywheel::velocity
            "theta" ids Hood::targetAngle
            "t" ids Globals::currentTime
        }
    }

}