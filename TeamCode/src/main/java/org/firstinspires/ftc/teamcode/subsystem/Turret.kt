package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.pid.PIDFController
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.D
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.P
import org.firstinspires.ftc.teamcode.subsystem.TurretConfig.F
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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

    val lowerLimit = -180.0
    val upperLimit = 180.0

    var fieldCentricAngle = 0.0

    val currentState get() = PvState(
        motor.position, motor.velocity
    )

    var targetState = PvState(0.0, 0.0)
        set(newState){
            field = PvState(
                State.DoubleState(
                    newState.position.toDouble().coerceIn(lowerLimit, upperLimit)),
                newState.velocity)
    }

    override val components = listOf<Component>(motor)

    val leftBound = PI
    val rightBound = 0.0

    // Init function, declare encoder
    init {
        motor.encoder = HardwareMap.turretEncoder(
            Component.Direction.FORWARD,
            ticksPerRev = 1.0, //TODO: tune
            wheelRadius = 1.0
        )

        HardwareMap.obeliskCamera(
            Vector2D(1280,720),
            Vector3D(0, 0, 0),
            YawPitchRollAngles(
                AngleUnit.RADIANS,
                0.0,
                0.0,
                0.0,
                0
            )
        )

    }

    // Update function
    override fun update(deltaTime: Double) {
        log("power") value motor.power
        log("position") value currentState.position

        if(usingFeedback){
            motor.compPower(
                PvState(targetState.position, -targetState.velocity)
                    .applyPD(P, D)
                    .toDouble()
            )
        }
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
            var a = cameraPos - Pose2D(cos(cameraPos.heading.toDouble()) * Globals.CameraOffsetB, sin(cameraPos.heading.toDouble()) * Globals.CameraOffsetB)
            botPos = a + Pose2D(cos(botPos.heading.toDouble()) * Globals.CameraOffsetA.x, sin(botPos.heading.toDouble()) * Globals.CameraOffsetA.x)

            TankDrivetrain.position = botPos

            Robot.readingTag = true
        }
        else Robot.readingTag = false

    } withEnd { Robot.readingTag = false }

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
        //keep the turret within the bounds
        if(theta().toDouble() > leftBound && theta().toDouble() < 3*PI/2){
            controller.targetPosition = leftBound
        }
        else if (theta().toDouble() < rightBound && theta().toDouble() > 7*PI/4) {
            controller.targetPosition = rightBound
        }
        else {
            controller.targetPosition = theta().toDouble()
        }

    } withEnd {
        motors.forEach { it.power = 0.0 }
        usingFeedback = false
    } withName "Tu: set angle"
}