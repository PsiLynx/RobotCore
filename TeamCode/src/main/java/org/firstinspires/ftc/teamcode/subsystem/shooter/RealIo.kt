package org.firstinspires.ftc.teamcode.subsystem.shooter

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.geometry.Range
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.valMap
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.RealSubsystemIo
import org.firstinspires.ftc.teamcode.util.degrees
import kotlin.math.PI

@Config
object FlywheelConfig {
    @JvmField var P = 6.0
    @JvmField var D = 0.5
    @JvmField var Ka = 0.0
    @JvmField var Ks = 0.0
    @JvmField var MAX_VEL = 410.0
}

@Config
object TurretConfig {
    @JvmField var P = 2.0
    @JvmField var D = 0.04
    @JvmField var F = 0.07
    @JvmField var A = 0.02
    @JvmField var V = 0.1
    @JvmField var servo1Offset = 0.0
    @JvmField var servo2Offset = 0.0
}

class RealIo: Io, RealSubsystemIo() {

    override fun update(deltaTime: Double) {
        super.update(deltaTime)

        // hood
        hoodServo.position = (
            0.1 + (
                ( hoodTargetAngle - hoodMinAngle ) * (0.95 - 0.1)
                    / (hoodMaxAngle - hoodMinAngle)
            )
        )

        // flywheel
        if (flywheelUsingFeedback) {
            listOf(flywheelRight, flywheelRight).forEach {
                it.compPower(
                    VaState(
                        linearVelToRotationalVel(
                            flywheelTargetState.velocity.toDouble()
                                    - flywheelCurrentState.velocity.toDouble()
                        ),
                        linearVelToRotationalVel(
                            flywheelCurrentState.acceleration.toDouble()
                        )
                    ).applyPD(FlywheelConfig.P, FlywheelConfig.D).toDouble()
                            + (
                            linearVelToRotationalVel(
                                flywheelTargetState.velocity.toDouble()
                            ) * (1 - FlywheelConfig.Ks) + FlywheelConfig.Ks
                            )
                            + (flywheelTargetState.acceleration * FlywheelConfig.Ka).toDouble()
                )
            }
        }

        // turret
        if (turretUsingFeedback) {
            setTurretAngle(turretTargetState.position)
        }
    }

    // hood
    private val hoodServo = HardwareMap.hood(range = Servo.Range.Default)
    override val hoodMinAngle = degrees(20)
    override val hoodMaxAngle = degrees(55)
    override var hoodTargetAngle: Double = hoodMinAngle
        set(value){
            field = (
                    if(value > hoodMaxAngle) hoodMaxAngle
                    else if(value < hoodMinAngle) hoodMinAngle
                    else value
                    )
        }

    //flywheel
    private val flywheelLeft = HardwareMap.shooterLeft(
        FORWARD,
        lowPassDampening = 0.5
    )
    private val flywheelRight = HardwareMap.shooterRight(
        REVERSE,
        lowPassDampening = 0.5
    )
    init {
        flywheelLeft.encoder = HardwareMap.shooterEncoder(REVERSE, 1.0)
        flywheelLeft.encoder!!.inPerTick = 1.0 / 2600
    }
    override var flywheelTargetState: VaState<State.DoubleState>
            = VaState(0.0, 0.0)
    private val flywheelCurrentState: VaState<State.DoubleState> get() = flywheelTargetState
    override fun flywheelSetPower(power: Double) {
        flywheelLeft.compPower(power)
        flywheelRight.compPower(power)
        flywheelUsingFeedback = false
    }
    override var flywheelUsingFeedback = false
    private fun rotationalVelToLinearVel(w: Double) = FlywheelConfig.MAX_VEL * w
    private fun linearVelToRotationalVel(v: Double) = v / FlywheelConfig.MAX_VEL


    //turret
    private val turretServo1 = HardwareMap.turret1()
    private val turretServo2 = HardwareMap.turret2()
    private var turretCanReachTarget = false
    override val turretLowerBound = Rotation2D(degrees(65))
    override val turretUpperBound = Rotation2D(degrees(335))
    override var turretTargetState: PvState<Rotation2D> = PvState(Rotation2D(PI), Rotation2D())
        set(value) {
            var theta = value.position
            if (theta > turretUpperBound) {
                theta = turretUpperBound
                turretCanReachTarget = false
            }
            else if(theta < turretLowerBound) {
                theta = turretLowerBound
                turretCanReachTarget = false
            }
            else {
                turretCanReachTarget = true
            }
            field = PvState(theta, value.velocity)
        }
    override var turretUsingFeedback = false
    fun setTurretAngle(angle: Rotation2D){
        turretServo1.position = valMap(
            angle.toDouble()+TurretConfig.servo1Offset,
            Range(turretLowerBound.toDouble(), turretUpperBound.toDouble()),
            Range(0.15,1)
        ).coerceIn(0.15, 1.0)
        turretServo2.position = valMap(
            angle.toDouble()+TurretConfig.servo2Offset,
            Range(turretLowerBound.toDouble(), turretUpperBound.toDouble()),
            Range(0.15,1)
        ).coerceIn(0.15, 1.0)
        turretUsingFeedback = false
    }

    override val components = listOf(
        flywheelRight,
        flywheelLeft,
        turretServo1,
        turretServo2,
        hoodServo
    )
}