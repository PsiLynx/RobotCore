package org.firstinspires.ftc.teamcode.subsystem.shooter

import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.sim.SimulatedPollen
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.transfer.Transfer
import org.firstinspires.ftc.teamcode.util.CacheValue
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.cos
import kotlin.math.sin
typealias TransferFakeIo = org.firstinspires.ftc.teamcode.subsystem.transfer.FakeIo

class FakeIo: Io {
    val SHOT_COOLDOWN = 0.2
    override var hoodTargetAngle = Rotation2D()
    override var flywheelTargetState = VaState(0.0, 0.0)
    override var positionEstimate = CacheValue<Pose2D>(Pose2D(), -1e10)

    private var lastShotTakenTime = -1e10

    override val flywheelCurrentState: VaState<State.DoubleState>
        get() = flywheelTargetState // TODO

    override fun flywheelSetPower(power: Double) {
        TODO("Not yet implemented")
    }

    override var flywheelUsingFeedback = false
    override var turretTargetState = PvState<Rotation2D>(
        Rotation2D(),
        Rotation2D()
    )
    override var turretUsingFeedback = false
    override fun update(deltaTime: Double) {
        if(
            Globals.currentTime - lastShotTakenTime > SHOT_COOLDOWN
            && (
                Transfer.io is TransferFakeIo
                && Transfer.io._power > 0
                && Transfer.io._blockerPos == Component.Opening.OPEN
            )
        ){
            lastShotTakenTime = Globals.currentTime

            val vel = Vector3D.fromSpherical(
                flywheelCurrentState.velocity,
                turretTargetState.position + Drivetrain.io.position.heading,
                hoodTargetAngle
            ) + Drivetrain.io.velocity.vector.threeD
            val pos = (
                Drivetrain.io.position.vector
                + (
                    (
                        (
                            Vector2D(
                                Shooter.flywheelRadius * sin(
                                    hoodTargetAngle.toDouble()
                                    - Shooter.flywheelEntrancePhi
                                ),
                                0
                            ) rotatedBy turretTargetState.position
                        )
                        + Shooter.turretOffset.groundPlane
                    )
                    rotatedBy Drivetrain.io.position.heading
                )
            )

            SimulatedPollen.newRecordedArtifact(
                Vector3D(
                    pos.x,
                    pos.y,
                    Shooter.turretOffset.z
                    + Shooter.flywheelRadius * (
                        cos(hoodTargetAngle.toDouble())
                        - cos(Shooter.flywheelEntrancePhi)
                    )
                ),
                vel
            )
        }
    }

    override fun reset() { }
}