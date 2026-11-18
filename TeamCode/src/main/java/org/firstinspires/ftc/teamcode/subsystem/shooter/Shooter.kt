package org.firstinspires.ftc.teamcode.subsystem.shooter

import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.log
import org.psilynx.psikit.core.mechanism.LoggedMechanism2d
import org.psilynx.psikit.core.mechanism.LoggedMechanismLigament2d
import org.psilynx.psikit.core.wpi.Color8Bit
import kotlin.math.PI

object Shooter: Subsystem<Shooter, Io>(RealIo(), FakeIo()) {

    val turretLowerBound = Rotation2D(degrees(-90))
    val turretUpperBound = Rotation2D(degrees(90))

    val hoodMinAngle = Rotation2D(degrees(5))
    val hoodMaxAngle = Rotation2D(degrees(40))

    var turretOffset = Vector3D(0.5, 0, 12)
    var flywheelRadius = 1.5
    var flywheelEntrancePhi = degrees(-35)

    override fun update(deltaTime: Double) {
        (
            hoodMechanism.root.objects().first() as LoggedMechanismLigament2d
        ).angle = (io.hoodTargetAngle.toDouble()) * 180 / PI

        log("positionEstimate") value io.positionEstimate.value
        log("hood/Mechanism") value hoodMechanism

        log("flywheel/vel") value io.flywheelCurrentState.velocity.value
        log("flywheel/acc") value io.flywheelCurrentState.acceleration.value

        log("turret/pos") value io.turretTargetState.position
        log("turret/vel") value io.turretTargetState.velocity
    }

    // hood
    val hoodMechanism = LoggedMechanism2d(0.5, 0.5).also {
        it.getRoot("hood", 0.15, 0.1)
        it.setBackgroundColor(Color8Bit("#000000"))
        it.root.append(LoggedMechanismLigament2d(
            "hood", 0.2, 0.0, 2.0, Color8Bit("#FFA500")
        ))
    }
    fun setHoodAngle(angle: () -> Rotation2D) = run {
        io.hoodTargetAngle = angle()
    } withInit { io.hoodTargetAngle = angle() } withName "Hd: setAngle"
    fun setHoodAngle(angle: Rotation2D) = setHoodAngle { angle } until { true }

    // flywheel
    fun flywheelRunAtVelocity(velocity: () -> Double)  = run {
        io.flywheelUsingFeedback = true
        io.flywheelTargetState = VaState(velocity(), 0.0)
    } withEnd {
        io.flywheelSetPower(0.0)
    } withName "Fw: runAtVel"
    fun flywheelRunAtVelocity(velocity: Double) = flywheelRunAtVelocity { velocity }


}