package org.firstinspires.ftc.teamcode.subsystem.robot

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.DeferredCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.Repeat
import org.firstinspires.ftc.teamcode.component.Component.Opening.CLOSED
import org.firstinspires.ftc.teamcode.component.Component.Opening.OPEN
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.sim.SimulatedArtifact
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.intake.Intake
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter
import org.firstinspires.ftc.teamcode.subsystem.transfer.Transfer
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
    fun kickBalls() = DeferredCommand {
       (
           Transfer.run(
               blockerPos = OPEN,
               motorPow = 0.0,
           )
           withTimeout 0.2
           andThen Transfer.run(
               blockerPos = OPEN,
               motorPow = RobotConfig.transferSpeed
           )
       )
    } withTimeout(2) withName "shoot balls" withDescription { "" }
}
@Config object RobotConfig {
    @JvmField var transferSpeed = 0.5
    @JvmField var rapidFireWait = 0.4
}
