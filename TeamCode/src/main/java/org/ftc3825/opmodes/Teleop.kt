package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.geometry.Pose2D
import kotlin.math.PI

@TeleOp(name = "FIELD CENTRIC")
class Teleop: CommandOpMode() {
    override fun init() {
        initialize()

        Drivetrain.reset()

        Drivetrain.position = Pose2D(10, 10, PI / 2)

        val driver = Gamepad(gamepad1!!)

        Drivetrain.run {
            it.setWeightedDrivePower(
                - driver.leftStickY.toDouble(),
                  driver.leftStickX.toDouble(),
                - driver.rightStickX.toDouble()
            )
        }.schedule()

        Telemetry.addAll {
            "pos" ids Drivetrain::position
            "vel" ids Drivetrain::velocity
        }
    }
}