package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.D
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.P
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.F
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.A
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

@Config
object TurretConfig {
    @JvmField var P = 2.0
    @JvmField var D = 0.03
    @JvmField var F = 0.1
    @JvmField var A = 0.07
}

object Turret: Subsystem<Turret>() {

    // Variables

    /**
     * The offset from the center of the ground plane of
     * the robot to the center of the turret horizontal
     * with the camera.
     **/
    var CameraOffsetA = Vector2D(-5,10)

    /**
     * The horizontal offset from the center of the turret to
     * the center of the camera.
     */
    var CameraOffsetB = 5
    
    
    var usingFeedback = false
    val angle get() = motor.angle

    val motor = HardwareMap.turret(Component.Direction.REVERSE)

    var velocity = 0.0
        private set

    val currentState get() = PvState(
        Rotation2D(motor.angle),
        Rotation2D(velocity)
    )
    private var lastPosition = Rotation2D(motor.angle)
    var canReachTarget = true

    val readyToShoot get() = (
        (targetState - currentState).position.absoluteMag()
        < 0.1
        && canReachTarget
    )

    var targetState: PvState<Rotation2D> = PvState(Rotation2D(PI), Rotation2D())
        set(value) {
            var theta = value.position
            if (theta > upperBound) {
                theta = upperBound
                canReachTarget = false
            }
            else if(theta < lowerBound) {
                theta = lowerBound
                canReachTarget = false
            }
            else {
                canReachTarget = true
            }
            field = PvState(theta, value.velocity)
    }
    override val components = listOf<Component>(motor)
    val lowerBound = Rotation2D(PI / 3)
    val upperBound = Rotation2D(2*PI - PI/3)

    init {
        motor.encoder = HardwareMap.turretEncoder(
            Component.Direction.FORWARD,
            ticksPerRev = 89856.0,
            wheelRadius = 1.0
        )
        motor.angle = PI

    }

    // Update function
    override fun update(deltaTime: Double) {
        velocity = (currentState.position - lastPosition).toDouble() / deltaTime
        lastPosition = currentState.position

        if(usingFeedback){
            var output = (
                PvState(
                    targetState.position - currentState.position,
                    currentState.velocity - targetState.velocity
                ).applyPD(P, D).toDouble()
                - (
                    if(
                        targetState.position - lowerBound > PI/4
                        && upperBound - targetState.position > PI/4
                    ) TankDrivetrain.velocity.heading.toDouble() * A
                    else 0.0
                )
            )
            output += F * output.sign
            log("power") value output.toDouble()
            motor.compPower(output)
        }

        log("ready to shoot") value readyToShoot
        log("target pos") value targetState.position.toDouble()
        log("target vel") value targetState.velocity.toDouble()
        log("current pos") value currentState.position.toDouble()
        log("current vel") value currentState.velocity.toDouble()
        log("position pose") value (
                TankDrivetrain.position + currentState.position
                )
        log("usingFeedback") value usingFeedback
        log("pose") value (
                TankDrivetrain.position
                        + currentState.position
                )
        log("ticks") value motor.encoder!!.posSupplier.asDouble
    }

    /**
     * This function will read any april tags, and then
     * do the inverse kinematics to find the position of
     * the robot.
     *
     * cameraPos: Pos2D() the position of the camera on the field.
     * botPos: Pos2D() the position of the robot on the field that
     *         has been derived from the position of the
     *         camera.
     *
     * a: Vector2D() The vector offset from the camera to the
     *               center of the turret
     */

    fun readAprilTags() = RunCommand {
        if(TankDrivetrain.tagReadGood){
            val cameraPos = Cameras.pose
            var botPos = Pose2D(0,0,angle - cameraPos.heading.toDouble())
            
            var a = cameraPos - Pose2D(
                cos(cameraPos.heading.toDouble()) * CameraOffsetB,
                sin(cameraPos.heading.toDouble()) * CameraOffsetB
            )

            botPos = a + Pose2D(
                cos(botPos.heading.toDouble()) * CameraOffsetA.x,
                sin(botPos.heading.toDouble()) * CameraOffsetA.x)

            TankDrivetrain.position = botPos

            Robot.readingTag = true
        }
        else Robot.readingTag = false

    } withEnd { Robot.readingTag = false }

    fun setAngle(theta: () -> Rotation2D) = run {
        usingFeedback = true
        //keep the turret within the bounds
    } withEnd {
        motors.forEach { it.power = 0.0 }
        usingFeedback = false
    } withName "Tu: set angle"
}