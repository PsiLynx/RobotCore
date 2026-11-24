package org.firstinspires.ftc.teamcode.subsystem.robot

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.DeferredCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Opening.OPEN
import org.firstinspires.ftc.teamcode.subsystem.intake.Intake
import org.firstinspires.ftc.teamcode.subsystem.robot.RobotConfig.lerpDownSeconds
import org.firstinspires.ftc.teamcode.subsystem.robot.RobotConfig.transferSpeed
import org.firstinspires.ftc.teamcode.subsystem.robot.RobotConfig.transferSpeedChange
import org.firstinspires.ftc.teamcode.subsystem.transfer.Transfer
import org.firstinspires.ftc.teamcode.subsystem.transfer.TransferConf
import org.firstinspires.ftc.teamcode.util.Globals

/**
 * this object serves a slightly higher level of abstraction than subsystems.
 * it provides an API for accessing information about, and commands that
 * interact with, multiple robot subsystems.
 *
 * Any commands that will be used in multiple places that require multiple
 * subsystems should be defined here.
 *
 * Additionally, any multi-subsystem based robot state triggers (e.g.
 * readyToShoot) should live here to provide a way to modify them easily.
 */
object Robot {
    //    val readyToShoot get() = Shooter.io.flywheelReadyToShoot && Shooter.io.turretReadyToShoot
    private var transferLerpStart = 0.0
    fun kickBalls() = DeferredCommand(Intake, Transfer) {
        (
            (
                (
                    Transfer.run(
                        blockerPos = OPEN,
                        motorPow = 0.0,
                    ) withTimeout 0.4
                      withName "SHOOT (open servo)"
                      withEnd {
                         transferLerpStart = Globals.currentTime
                      }
                ) andThen (
                    Transfer.run {
                        io.setPower(
                            transferSpeed
                                - (transferSpeedChange / lerpDownSeconds)
                                * (Globals.currentTime - transferLerpStart)
                        )
                    } withName "SHOOT (lerp)" withTimeout 2
                ) andThen (Transfer.run(OPEN, 0.7) withName "SHOOT (constant")
            ) parallelTo Intake.run() withName ""
        ) withTimeout (3) withEnd {
            Transfer.io.setPower(0.0)
            Intake.io.setPower(0.0)
        }
    }
    fun intake() = DeferredCommand(Intake, Transfer) {
        Intake.run() parallelTo Transfer.run(
            Component.Opening.CLOSED,
            TransferConf.IntakeTransferSpeed
            )
        } withName "intake balls"
    }



@Config object RobotConfig {
    @JvmField var transferSpeed = 0.9
    @JvmField var rapidFireWait = 0.4
    @JvmField var lerpDownSeconds = 3.0
    @JvmField var transferSpeedChange = 0.3
}
