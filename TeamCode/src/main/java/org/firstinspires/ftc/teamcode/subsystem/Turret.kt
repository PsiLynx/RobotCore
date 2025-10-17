package org.firstinspires.ftc.teamcode.subsystem

import androidx.lifecycle.Lifecycle
import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.State.DoubleState
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain.position
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.D
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.P
import org.firstinspires.ftc.teamcode.subsystem.Intake.setPower
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.F
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.Tunable
import org.firstinspires.ftc.teamcode.util.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sign

@Config
object TurretConfig {
    @JvmField var P = 4.05
    @JvmField var D = 0.0
    @JvmField var F = 0.57
}

object Turret: Subsystem<Turret>() {

    // Variables
    var usingFeedback = false
    val angle get() = motor.angle

    val motor = HardwareMap.turret(Component.Direction.FORWARD)

    val velocity get() = Turret.motor.velocity
    val acceleration get() = Turret.motor.acceleration

    override val components = listOf<Component>(motor)

    // Init function, declare encoder
    init {
        motor.encoder = HardwareMap.turretEncoder(Component.Direction.FORWARD, 1.0, 1.0)
    }

    // Update function
    override fun update(deltaTime: Double) {
        log("power") value Intake.motor.power
    }

    //
    val controller = PIDFController(
        P = { P },
        D = { D },
        relF = { F },
        targetPosition = 0.0,
        pos = { this@Turret.angle },
        setpointError = {
            arrayListOf(
                targetPosition - position.heading.toDouble(),
                targetPosition - position.heading.toDouble() + 2*PI,
                targetPosition - position.heading.toDouble() - 2*PI,
            ).minBy { abs(it) } // smallest absolute value with wraparound
        },
        apply = { Turret.motor.compPower(it) },
    )

    fun update() {
        log("angle") value angle
        log("controller") value controller
        log("usingFeedback") value usingFeedback
    }

    fun setAngle(theta: () -> Rotation2D) = run {
        usingFeedback = true
        controller.targetPosition = theta().toDouble()
    } withEnd {
        motors.forEach { it.power = 0.0 }
        usingFeedback = false
    }

}