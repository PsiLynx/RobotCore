package org.firstinspires.ftc.teamcode.subsystem.shooter

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.log
import org.psilynx.psikit.core.mechanism.LoggedMechanism2d
import org.psilynx.psikit.core.mechanism.LoggedMechanismLigament2d
import org.psilynx.psikit.core.wpi.Color8Bit
import kotlin.math.PI

object Shooter: Subsystem<Shooter, Io>(RealIo(), FakeIo()) {

    override fun update(deltaTime: Double) {
        (
            mechanism.root.objects().first() as LoggedMechanismLigament2d
        ).angle = (io.hoodTargetAngle) * 180 / PI

        log("positionEstimate") value io.positionEstimate.value
        log("mechanism") value mechanism
        // if (!logged(thing)) {
        //     log(thing)
        // }
    }

    // hood
    val mechanism = LoggedMechanism2d(0.5, 0.5).also {
        it.getRoot("hood", 0.15, 0.1)
        it.setBackgroundColor(Color8Bit("#000000"))
        it.root.append(LoggedMechanismLigament2d(
            "hood", 0.2, 0.0, 2.0, Color8Bit("#FFA500")
        ))
    }
    fun setHoodAngle(angle: () -> Double) = run {
        io.hoodTargetAngle = angle()
    } withInit { io.hoodTargetAngle = angle() } withName "Hd: setAngle"
    fun setHoodAngle(angle: Double) = setHoodAngle { angle } until { true }

    // flywheel
    fun flywheelRunAtVelocity(velocity: () -> Double)  = run {
        io.flywheelUsingFeedback = true
        io.flywheelTargetState = VaState(velocity(), 0.0)
    } withEnd {
        io.flywheelSetPower(0.0)
    } withName "Fw: runAtVel"
    fun flywheelRunAtVelocity(velocity: Double) = flywheelRunAtVelocity { velocity }


}