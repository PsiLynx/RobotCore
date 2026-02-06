package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.DeferredCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.Trigger
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.Repeat
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.While
import org.firstinspires.ftc.teamcode.component.Component.Opening.CLOSED
import org.firstinspires.ftc.teamcode.component.Component.Opening.OPEN
import org.firstinspires.ftc.teamcode.shooter.CompTargets
import org.firstinspires.ftc.teamcode.shooter.ShooterConfig
import org.firstinspires.ftc.teamcode.util.Globals

/**
 * this object serves a slightly higher level of abstraction than subsystems.
 * it provides an API for accessing information about, and commands that
 * interact with, multiple robot subsystems.
 *
 * Any commands that will be used in multiple places that require multiple
 * subsystems should be defined here.
 *
 * Additionally, any multi-subsystem based robot state triggers (e.g.
 * readyToShoot) should live here to provide a way to modify them easily.
 */
object Robot {
    val readyToShoot get() = Flywheel.readyToShoot && Turret.readyToShoot
    var readingTag = false

    fun kickBalls() = (
        Repeat(times=3) {(
            Intake.run(
                propellerPos = CLOSED,
                blockerPos = OPEN,
                motorPow = 1.0,
                transferSpeed = 0.9,
            )
            until { Flywheel.justShot }
            andThen DeferredCommand {
                WaitCommand(
                    (TankDrivetrain.position - CompTargets.goalPos2D).vector.mag
                    / 101.0
                    / 15
                )
            }
        )}
    ) withTimeout(2) withName "shoot balls" withDescription { "" }


    class RightTriggerManager(val trigger: Trigger): Command() {
        var triggeredTime = 0.0
        val triggered get() = trigger.supplier.asBoolean
        var kickBallsShouldEnd = false
        var command: Command? = null
        override fun execute() {
            if(triggered && triggeredTime == 0.0){
                triggeredTime = Globals.currentTime
                command = (
                    (
                        While({kickBallsShouldEnd == false}, (
                            Intake.run(
                                propellerPos = CLOSED,
                                blockerPos = OPEN,
                                motorPow = 1.0,
                                transferSpeed = 0.9,
                            )
                            until { Flywheel.justShot }
                            andThen DeferredCommand {
                                WaitCommand(
                                    (TankDrivetrain.position - CompTargets.goalPos2D).vector.mag
                                    / 101.0
                                    / 15
                                )
                            }
                        ))
                    ) withTimeout(2) withName "shoot balls" withDescription { "" }
                )
                command!!.schedule()
            }

            if(!triggered){
                if(Globals.currentTime - triggeredTime < 0.5){
                    kickBallsShouldEnd = true
                }
                else {
                    if(command != null) {
                        CommandScheduler.end(command!!)
                    }
                    command = null
                }
                triggeredTime = 0.0
            }
        }

        override fun isFinished() = false
    }
}