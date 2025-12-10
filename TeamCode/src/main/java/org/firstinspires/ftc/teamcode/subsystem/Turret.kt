package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.D
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.P
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.F
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.abs

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

    var fieldCentricAngle = 0.0

    val position get() = motor.position
    val velocity get() = motor.velocity

    override val components = listOf<Component>(motor)

    // Init function, declare encoder
    init {
        motor.encoder = HardwareMap.turretEncoder(
            Component.Direction.FORWARD,
            ticksPerRev = 1.0, //TODO: tune
            wheelRadius = 1.0
        )
    }

    // Update function
    override fun update(deltaTime: Double) {
        log("power") value motor.power
        log("position") value position

        if(usingFeedback){
            controller.updateController(deltaTime)
        }
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
                targetPosition - motor.angle.toDouble(),
                targetPosition - motor.angle.toDouble() + 2*PI,
                targetPosition - motor.angle.toDouble() - 2*PI,
            ).minBy { abs(it) } // smallest absolute value with wraparound
        },
        apply = { motor.compPower(it) },
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