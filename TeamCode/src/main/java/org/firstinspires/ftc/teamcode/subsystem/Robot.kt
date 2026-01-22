package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.Repeat
import org.firstinspires.ftc.teamcode.component.Component.Opening.CLOSED
import org.firstinspires.ftc.teamcode.component.Component.Opening.OPEN

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
    val readyToShoot get() = Flywheel.readyToShoot //&& Turret.readyToShoot
    var readingTag = false

    fun kickBalls() = Repeat(times=2) {(
        Intake.run(
            propellerPos = CLOSED,
            blockerPos   = OPEN,
            motorPow = 1.0

        ) until { Flywheel.justShot }
        withTimeout 2
    )} andThen WaitCommand(0.1) andThen (
        Intake.run(
            propellerPos = CLOSED,
            blockerPos   = OPEN,
            motorPow = 1.0
        ) until { Flywheel.justShot }
        withTimeout 2
    )
}

@Config object RobotConfig {
    @JvmField var shootingIntakeSpeed = 0.8
}