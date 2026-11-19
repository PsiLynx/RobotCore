package org.firstinspires.ftc.teamcode.subsystem.robot

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.DeferredCommand
import org.firstinspires.ftc.teamcode.component.Component.Opening.OPEN
import org.firstinspires.ftc.teamcode.subsystem.intake.Intake
import org.firstinspires.ftc.teamcode.subsystem.transfer.Transfer

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
    fun kickBalls() = DeferredCommand(Transfer) {
//    fun kickBalls() = (
        (
            Transfer.run(
                blockerPos = OPEN,
                motorPow = 0.0,
            )
                    withTimeout 0.2
                    andThen Transfer.run(
                blockerPos = OPEN,
                motorPow = RobotConfig.transferSpeed
            ) parallelTo Intake.run()
        )
    } withTimeout (3) withName "shoot balls" withDescription { "" }

}

@Config object RobotConfig {
    @JvmField var transferSpeed = 0.8
    @JvmField var rapidFireWait = 0.4
}
