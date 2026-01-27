package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI

class TeleopDrivePowers(
    val driver: Gamepad,
    val operator: Gamepad,
): Command() {
    override val requirements = mutableSetOf<Subsystem<*>>(TankDrivetrain)

    override fun isFinished() = false
    override fun execute() = with(TankDrivetrain) {

        val targetHeading = (
            ShootingStateOTM.goalPose.groundPlane - position.vector
        ).theta + Rotation2D(PI)
        log("target heading") value targetHeading.toDouble()
        log("robot theta + PI") value (position.heading.toDouble() + PI)

        val drive  = - driver.leftStick.y.sq
        val turn   = - driver.rightStick.x.cube * 7/8
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

        setWeightedDrivePower(
            drive = drive,
            turn  = turn,
            slew = true,
        )
    }
}