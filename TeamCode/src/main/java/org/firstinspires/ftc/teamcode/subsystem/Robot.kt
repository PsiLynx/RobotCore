package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.DeferredCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.Trigger
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.Repeat
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.While
import org.firstinspires.ftc.teamcode.component.Component.Opening.CLOSED
import org.firstinspires.ftc.teamcode.component.Component.Opening.OPEN
import org.firstinspires.ftc.teamcode.shooter.CompTargets
import org.firstinspires.ftc.teamcode.shooter.ShooterConfig
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
    val readyToShoot get() = Flywheel.readyToShoot && Turret.readyToShoot
    var readingTag = false

    fun kickBalls() = (
        Repeat(times=3) {(
            Intake.run(
                propellerPos = CLOSED,
                blockerPos = OPEN,
                motorPow = 1.0,
                transferSpeed = 1.0,
            )
            until { Flywheel.justShot }
            andThen DeferredCommand {
                WaitCommand(RobotConfig.rapidFireWait)
            }
            //andThen WaitUntilCommand(Flywheel::readyToShoot)
        )}
    ) withTimeout(2) withName "shoot balls" withDescription { "" }
}
@Config object RobotConfig {
    @JvmField var rapidFireWait = 0.3
}