package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.DeferredCommand
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.Repeat
import org.firstinspires.ftc.teamcode.component.Component.Opening.CLOSED
import org.firstinspires.ftc.teamcode.component.Component.Opening.OPEN
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.shooter.ShooterConfig
import org.firstinspires.ftc.teamcode.sim.SimulatedArtifact
import org.firstinspires.ftc.teamcode.sim.TestClass
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
        if(Globals.unitTesting == false) (
            Repeat(times=3) {(
                Intake.run(
                    propellerPos = CLOSED,
                    blockerPos = OPEN,
                    motorPow = 1.0,
                    transferSpeed = 0.5,
                )
//                    until { Flywheel.justShot }
//                    andThen DeferredCommand {
//                        WaitCommand(RobotConfig.rapidFireWait)
//                    }
            )}
        )
        else (
            Repeat(3) {(
                InstantCommand {
                    val position2d = (
                        ShooterConfig.flywheelOffset.groundPlane
                        rotatedBy TankDrivetrain.position.heading
                    ) + TankDrivetrain.position.vector
                    SimulatedArtifact.newRecordedArtifact(
                        Vector3D(
                            position2d.x,
                            position2d.y,
                            ShooterConfig.flywheelOffset.z
                        ),
                        Vector3D.fromSpherical(
                            Flywheel.currentState.velocity,
                            (
                                TankDrivetrain.position.heading
                                + Turret.currentState.position
                            ),
                            Rotation2D(Hood.targetAngle)
                        ) + Vector3D(
                            TankDrivetrain.velocity.x,
                            TankDrivetrain.velocity.y,
                            0.0
                        )
                    )
                    Flywheel.motors.forEach {
                        FakeMotor.fromDcMotor(
                            it.hardwareDevice as DcMotor
                        ).speed *= 0.9
                    }
                }
                andThen WaitCommand(RobotConfig.rapidFireWait)
            )}
        )
    ) withTimeout(2) withName "shoot balls" withDescription { "" }
}
@Config object RobotConfig {
    @JvmField var rapidFireWait = 0.3
}