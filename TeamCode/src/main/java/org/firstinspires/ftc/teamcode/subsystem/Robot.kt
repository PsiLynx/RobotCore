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
    var readingTag = false

    fun kickBalls() = (
        Kicker.close()
        andThen ( Intake.setPower(1.0) withTimeout 0.6 )
        andThen Kicker.open()
        andThen ( Intake.setPower(1.0) withTimeout 0.6 )
        andThen Kicker.close()
        andThen WaitCommand(1.0)
        andThen Kicker.open()
    ) withEnd Kicker.open()
}
