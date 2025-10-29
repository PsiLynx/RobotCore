package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig

@TeleOp
class TuneFlywheelVel: CommandOpMode() {
    override fun afterSelect() {
        var max = FlywheelConfig.MAX_VEL
        var min = 0.0

        driver.apply {
            a.onTrue(InstantCommand {
                max /= 1.2
                min /= 1.2
            })
            y.onTrue(InstantCommand {
                max *= 1.2
                min *= 1.2
            })

            dpadUp.  onTrue( Flywheel.runAtVelocity{max} )
            dpadDown.onTrue( Flywheel.runAtVelocity{min} )
        }
    }
}