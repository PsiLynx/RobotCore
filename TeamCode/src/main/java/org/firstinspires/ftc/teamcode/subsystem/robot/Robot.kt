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
import org.firstinspires.ftc.teamcode.shooter.ShooterConfig
import org.firstinspires.ftc.teamcode.sim.SimulatedArtifact
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.intake.Intake
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter
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
    var readingTag = false

    fun kickBalls() = (
        if(Globals.unitTesting == false) (
           ( Intake.run(
                blockerPos = OPEN,
                motorPow = 0.0,
           ) withTimeout 0.2 )
           andThen Intake.run(
                blockerPos = OPEN,
                motorPow = RobotConfig.transferSpeed,
           )
        )
        else (
            Repeat(3) {(
                InstantCommand {
                    val position2d = (
                        ShooterConfig.flywheelOffset.groundPlane
                        rotatedBy Drivetrain.io.position.heading
                    ) + Drivetrain.io.position.vector
                    SimulatedArtifact.newRecordedArtifact(
                        Vector3D(
                            position2d.x,
                            position2d.y,
                            ShooterConfig.flywheelOffset.z
                        ),
                        Vector3D.fromSpherical(
                            Shooter.io.flywheelCurrentState.velocity,
                            (
                                Drivetrain.io.position.heading
                                + Shooter.io.turretTargetState.position
                            ),
                            Rotation2D(Shooter.io.hoodTargetAngle)
                        ) + Vector3D(
                            Drivetrain.io.velocity.x,
                            Drivetrain.io.velocity.y,
                            0.0
                        )
                    )
                }
                andThen WaitCommand(RobotConfig.rapidFireWait)
            )}
        )
    ) withTimeout(2) withName "shoot balls" withDescription { "" }
}
@Config object RobotConfig {
    @JvmField var transferSpeed = 0.7
    @JvmField var rapidFireWait = 0.4
}
