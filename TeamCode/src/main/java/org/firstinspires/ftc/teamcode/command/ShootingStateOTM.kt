package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Hood
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.log
import org.firstinspires.ftc.teamcode.shooter.ComputeTraj
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Turret
import org.firstinspires.ftc.teamcode.shooter.ShooterConfig
import org.firstinspires.ftc.teamcode.shooter.CompTargets
import kotlin.math.PI

/**
 * Beware! Even the action of looking and attempting to comprehend
 * these programs may cause minor yet persistent brain damage due to unnecessary
 * complexity!
 * You have been warned!
 *
 * This class is responsible for consoling the flywheel speed and the hood angle
 * Desmos graphs demonstrating the basic concepts can be found at
 * https://www.desmos.com/calculator/jaxgormzj1
 */
class ShootingStateOTM(
    var fromPos: () -> Pose2D = { TankDrivetrain.position },
    var botVel: () -> Pose2D = { TankDrivetrain.velocity },
    var target: () -> Vector3D = { CompTargets.compGoalPos(fromPos()) },
    var flywheelVel: () -> Double = { Flywheel.currentState.velocity.value },
    var flywheelAcc:() -> Double = { Flywheel.currentState.acceleration.value },
    var futureDT: Double = 0.1,
    var futurePos: () -> Pose2D = { TankDrivetrain.futurePos(futureDT)},
) : Command() {

    override val requirements = mutableSetOf<Subsystem<*>>(Hood, Flywheel, Turret)

    override fun initialize() {
        /** Using feedback sets the PID controller active. */
        Flywheel.usingFeedback = true
        Turret.usingFeedback = true
    }

    override fun execute() {

        val targetVec: Vector3D = ComputeTraj.compLaunchVec(
            target(),
            fromPos(),
            botVel(),
        )

        val futureTargetVec: Vector3D = ComputeTraj.compLaunchVec(
            target(),
            futurePos(),
            botVel(),
        )

        val velVec: Vector3D = ComputeTraj.compFlywheelDependantVec(
            target(),
            Vector3D(fromPos().x, fromPos().y, ShooterConfig.flywheelOffset.z),
            botVel(),
            flywheelVel()
        )

        /**now parse and command the flywheel, hood, and turret
         * based on which flags are set, weather to activate the
         * turret or hood.
         */

        //Setting Flywheel and Hood
        if (!ShooterConfig.flywheelDisabled) {
            Flywheel.targetState = VaState(
                targetVec.mag,
                (futureTargetVec.mag - targetVec.mag) / futureDT
            )

            Hood.targetAngle = PI / 2 - velVec.verticalAngle.toDouble()
        } else {
            Hood.targetAngle = PI / 2 - 30 * PI / 180
            Flywheel.targetState = VaState(0.7, 0.0)
        }

        //Setting Turret
        if (!ShooterConfig.turretDisabled) {
            Turret.motors.forEach {
                it.setZeroPowerBehavior(Motor.ZeroPower.FLOAT)
            }
            Turret.targetState = PvState(
                (
                        velVec.horizontalAngle
                                - TankDrivetrain.position.heading
                        ).wrap(),

                -TankDrivetrain.velocity.heading
            )
        } else {
            Turret.motors.forEach {
                it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
                it.power = 0.0
            }
        }


        log("targetVelocity") value targetVec.mag
        log("launchAngle") value targetVec.verticalAngle

        log("launchVec") value targetVec
        log("FlywheelVelocityWithRBmotion") value Flywheel.currentState.velocity
        log("MovingVertAngle") value Hood.targetAngle

    }

    override fun end(interrupted: Boolean){
        /**
         * Command flywheels to stop using feedback control.
         * Set flywheel power to 0 and hood angle to 0.
         */
        Flywheel.usingFeedback = false
        Turret.usingFeedback = false
        Flywheel.motors.forEach { it.power = 0.0 }
        Turret.motors.forEach { it.power = 0.0 }
        Hood.setAngle(Hood.minAngle)
    }

    override var name = { "ShootingState" }
}