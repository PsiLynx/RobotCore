package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.subsystem.TankDriveConf
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Turret
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.log
import java.util.function.BooleanSupplier
import java.util.function.DoubleSupplier
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow

class TeleopDrivePowers(
    val driver: Gamepad,
    val operator: Gamepad,
): Command() {
    override val requirements = mutableSetOf<Subsystem<*>>(TankDrivetrain)

    override fun isFinished() = false
    override fun execute() = with(TankDrivetrain) {

        val targetHeading = (
            Globals.goalPose.groundPlane - position.vector
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