package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.shooter.goalPos
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI
import kotlin.math.abs

class TeleopDrivePowers(
    val driver: Gamepad,
    val operator: Gamepad,
): Command() {
    override val requirements = mutableSetOf<Subsystem<*>>(TankDrivetrain)

    override fun isFinished() = false
    override fun execute() = with(TankDrivetrain) {

        val targetHeading = (
                goalPos.compGoalPos(position).groundPlane - position.vector
        ).theta + Rotation2D(PI)
        log("target heading") value targetHeading.toDouble()
        log("robot theta + PI") value (position.heading.toDouble() + PI)

        val drive  = - driver.leftStick.y.sq
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
        setWeightedDrivePower(
            drive = drive,
            turn  = turn * (if(slow) 0.5 else 1.0),
            slew = true,
        )
    }
}