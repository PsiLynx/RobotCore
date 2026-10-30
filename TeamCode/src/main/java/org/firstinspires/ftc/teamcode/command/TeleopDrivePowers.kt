package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.shooter.CompTargets
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.abs

class TeleopDrivePowers(
    val driver: Gamepad,
    val operator: Gamepad,
): Command() {
    override val requirements = mutableSetOf<Subsystem<*,*>>(Drivetrain)

    override fun isFinished() = false
    override fun execute() {

//        val targetHeading = (
//                CompTargets.compGoalPos(position).groundPlane - position.vector
//        ).theta + Rotation2D(PI)
//        log("target heading") value targetHeading.toDouble()
//        log("robot theta + PI") value (position.heading.toDouble() + PI)

        var turn   = - driver.rightStick.x.cube * 7/8
        if(abs(turn) < 0.05) { turn = 0.0 }
        val slow   =  driver.rightStick.supplier.asBoolean
        /*
            else {
                PvState(
                    arrayListOf(
                         targetHeading - position.heading,
                         targetHeading - position.heading + Rotation2D(2 * PI),
                         targetHeading - position.heading - Rotation2D(2 * PI),
                    ).minBy { abs(it.toDouble()) },

                    velocity.heading
                ).applyPD(
                    TankDriveConf.P,
                    TankDriveConf.D,
                ).toDouble()
            }
        )
         */

        log("slow mode") value slow
        Drivetrain.io.drivePowers(
            ChassisSpeeds(
                + driver.leftStick.x.sq,
                - driver.leftStick.y.sq,
                if (slow) turn * 0.5 else turn,
            )
        )
    }
}