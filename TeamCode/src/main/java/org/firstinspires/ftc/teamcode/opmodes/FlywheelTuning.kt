package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.DeferredCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.subsystem.robot.Robot
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter
import org.firstinspires.ftc.teamcode.subsystem.transfer.Transfer

@TeleOp()
class FlywheelTuning: CommandOpMode() {
    override fun postSelector() {
        val flywheelCycle = CyclicalCommand(
            Shooter.run { it.io.flywheelSetPower(0.0) },

            ShootingStateOTM()
        )
        driver.apply {
            leftBumper.whileTrue(Transfer.run(Component.Opening.CLOSED))

            rightBumper.onTrue(flywheelCycle.nextCommand())

            rightTrigger.onTrue(Robot.kickBalls())
        }
    }
}