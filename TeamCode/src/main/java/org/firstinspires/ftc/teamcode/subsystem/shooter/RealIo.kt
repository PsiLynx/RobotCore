package org.firstinspires.ftc.teamcode.subsystem.shooter

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles
import org.firstinspires.ftc.teamcode.command.ShootingConf
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Range
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.geometry.valMap
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.internal.RealSubsystemIo
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter
import org.firstinspires.ftc.teamcode.util.CacheValue
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI

@Config
object FlywheelConfig {
    @JvmField var P = 6.0
    @JvmField var D = 0.0
    @JvmField var Ka = 0.0
    @JvmField var Ks = 0.0
    @JvmField var MAX_VEL = 300.0
    @JvmField var IN_PER_S_PER_RAD = 40
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

    // camera
    /*
    val turretCamera = HardwareMap.turretCamera(
        Vector2D(1280, 720),
        Vector3D(0, 0, 0),
        YawPitchRollAngles(
            AngleUnit.DEGREES,
            0.0, 37.5, 0.0, 0L
        ),
        arrayOf(
            907.1245535, 909.8575333, 635.9724113, 368.9964039
        )

    )

     */

    override var positionEstimate = CacheValue<Pose2D>(Pose2D(), -1e10)

    override fun update(deltaTime: Double) {
        super.update(deltaTime)

        // hood
        hoodServo.position = (
            0.1 + (
                ( hoodTargetAngle - Shooter.hoodMinAngle ).toDouble()
                * (0.95 - 0.1)
                / (Shooter.hoodMaxAngle - Shooter.hoodMinAngle).toDouble()
            )
        )

        // flywheel
        Shooter.log("flywheel speed ticks") value (
            flywheelLeft.encoder!!.velSupplier(0.0)
        )
        if (flywheelUsingFeedback) {
            listOf(flywheelLeft, flywheelRight).forEach {
                val power = (
                    VaState(
                        (
                            flywheelTargetState.velocity.toDouble()
                            - flywheelCurrentState.velocity.toDouble()
                        ) / FlywheelConfig.MAX_VEL,
                        0.0 // TODO: fix
                    ).applyPD(FlywheelConfig.P, FlywheelConfig.D).toDouble()
                    + (
                        linearVelToRotationalVel(
                            flywheelTargetState.velocity.toDouble()
                        )
                    )
                    + (flywheelTargetState.acceleration * FlywheelConfig.Ka).toDouble()
                )
                it.compPower(power)
                Shooter.log("flywheel/power") value power
                Shooter.log("flywheel/feedforward") value (
                    linearVelToRotationalVel(
                        flywheelTargetState.velocity.toDouble()
                    )
                )
            }
        }

        // turret
        Shooter.log("turret using feedback") value turretUsingFeedback
        if (turretUsingFeedback) {
            setTurretAngle(turretTargetState.position)
        }

        /*
        log("detections") value (
            turretCamera.detections?.size?.toString() ?: "null"
        )

        turretCamera.detections?.forEach { tag ->
            if(tag == null || tag.robotPose == null) return@forEach
            print("detection! ${tag.id}")

            positionEstimate = CacheValue(
                Pose2D(
                    tag.robotPose.position.x,
                    tag.robotPose.position.y,
                    tag.robotPose.orientation.yaw
                ) - (
                    Vector2D(4.03, 0)
                    rotatedBy (
                        turretTargetState.position
                        + Drivetrain.io.position.heading

                    )
                ) - (
                    ShootingConf.turretOffset.groundPlane
                    rotatedBy Drivetrain.io.position.heading
                )
                - (
                    turretTargetState.position
                )
            )

        }
         */
    }

    // hood
    private val hoodServo = HardwareMap.hood(range = Servo.Range.Default)
    override var hoodTargetAngle = Rotation2D()
        set(value){
            field = (
                if(value > Shooter.hoodMaxAngle) Shooter.hoodMaxAngle
                else if(value < Shooter.hoodMinAngle) Shooter.hoodMinAngle
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
        flywheelLeft.encoder!!.inPerTick = 1.0 / -2460
        //turretCamera.build()
        //turretCamera.enable()
    }
    override var flywheelTargetState: VaState<State.DoubleState>
            = VaState(0.0, 0.0)
    override val flywheelCurrentState: VaState<State.DoubleState>
        get() = VaState(
            rotationalVelToLinearVel(flywheelLeft.velocity),
            rotationalVelToLinearVel(flywheelLeft.acceleration)
        )
    override fun flywheelSetPower(power: Double) {
        flywheelLeft.compPower(power)
        flywheelRight.compPower(power)
        flywheelUsingFeedback = false
    }
    override var flywheelUsingFeedback = false

    private fun rotationalVelToLinearVel(w: Double) = (
        w * FlywheelConfig.MAX_VEL + (
            FlywheelConfig.IN_PER_S_PER_RAD
            * ( hoodTargetAngle - Shooter.hoodMinAngle).toDouble()
        )
    )

    private fun linearVelToRotationalVel(v: Double)
        = (
            v - (
                FlywheelConfig.IN_PER_S_PER_RAD
                * ( hoodTargetAngle - Shooter.hoodMinAngle).toDouble()
            )
        ) / FlywheelConfig.MAX_VEL


    //turret
    private val turretServo1 = HardwareMap.turret1()
    private val turretServo2 = HardwareMap.turret2()
    private var turretCanReachTarget = false
    override var turretTargetState: PvState<Rotation2D> = PvState(Rotation2D(PI), Rotation2D())
        set(value) {
            var theta = value.position.normalized()
            if (theta > Shooter.turretUpperBound) {
                theta = Shooter.turretUpperBound
                turretCanReachTarget = false
            }
            else if(theta < Shooter.turretLowerBound) {
                theta = Shooter.turretLowerBound
                turretCanReachTarget = false
            }
            else {
                turretCanReachTarget = true
            }
            field = PvState(theta, value.velocity)
        }
    override var turretUsingFeedback = false
    fun setTurretAngle(angle: Rotation2D){
        val turretLeftServoPos = 0.78
        val turretRightServoPos = 0.22
        turretServo1.position = valMap(
            angle.normalized().toDouble(),
            Range(Shooter.turretLowerBound.toDouble(), Shooter.turretUpperBound.toDouble()),
            Range(turretRightServoPos, turretLeftServoPos)
        ).coerceIn(turretRightServoPos, turretLeftServoPos) + TurretConfig.servo1Offset
        turretServo2.position = valMap(
            angle.normalized().toDouble(),
            Range(Shooter.turretLowerBound.toDouble(), Shooter.turretUpperBound.toDouble()),
            Range(turretRightServoPos, turretLeftServoPos)
        ).coerceIn(turretRightServoPos, turretLeftServoPos) + TurretConfig.servo2Offset
        Shooter.log("turret servo angle") value turretServo1.position
        // turretUsingFeedback = false
    }

    override val components = listOf(
        flywheelRight,
        flywheelLeft,
        turretServo1,
        turretServo2,
        hoodServo
    )
}