package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.command.internal.WaitCommand

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
    val readyToShoot get() = Flywheel.readyToShoot && Drivetrain.readyToShoot

    fun kickBall() = (
        Kicker.close()
        andThen WaitCommand(1.3)
        andThen Kicker.open()
        andThen WaitCommand(1)
        andThen (
            Intake.run()
            until { Kicker.pressed }
            withTimeout 3
        )
    )
}