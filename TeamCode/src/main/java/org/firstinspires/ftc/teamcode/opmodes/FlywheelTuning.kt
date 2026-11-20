package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.CyclicalCommand
import org.firstinspires.ftc.teamcode.command.internal.DeferredCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.intake.Intake
import org.firstinspires.ftc.teamcode.subsystem.robot.Robot
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter
import org.firstinspires.ftc.teamcode.subsystem.transfer.Transfer
import org.firstinspires.ftc.teamcode.subsystem.transfer.TransferConf
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.PI

@Config
object TuningConfig {
}


@TeleOp()
class FlywheelTuning: CommandOpMode() {
    override fun preSelector() {
        Drivetrain.io.position = (
            Pose2D(0, -72, PI / 2) + Pose2D(-15.25, 15.25) / 2
        )
        Globals.alliance = Globals.Alliance.RED
    }
    override fun postSelector() {
        val flywheelCycle = CyclicalCommand(
            Shooter.run { io.flywheelSetPower(0.0) },

            ShootingStateOTM()
        )
        Drivetrain.justUpdate().schedule()

        driver.apply {
            leftBumper.whileTrue(Transfer.run(Component.Opening.CLOSED) parallelTo Intake.run())

            leftTrigger.whileTrue(
                DeferredCommand(Intake, Transfer) {
                    Intake.run() parallelTo Transfer.run(
                        Component.Opening.CLOSED,
                        TransferConf.IntakeTransferSpeed
                    )
                }
            )

            rightBumper.onTrue(flywheelCycle.nextCommand())

            rightTrigger.whileTrue(Robot.kickBalls())
//            rightTrigger.whileTrue((Transfer.run(Component.Opening.OPEN, 0.0) withTimeout(0.2)) andThen Transfer.run(Component.Opening.OPEN, 1.0) parallelTo Intake.run())
        }
    }
}