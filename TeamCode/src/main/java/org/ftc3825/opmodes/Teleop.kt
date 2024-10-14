package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Pose2D

@TeleOp(name = "TELEOP", group = "a")
class Teleop: CommandOpMode() {

    override fun init() {
        initialize()
        Telemetry.telemetry = telemetry

        var driver = Gamepad(gamepad1!!)
        var operator = Gamepad(gamepad2!!)

        var servo = hardwareMap.get(Servo::class.java, "claw")

        Drivetrain.run {
            it.setWeightedDrivePower(Pose2D(
                    driver.left_stick_y,
                    -driver.left_stick_x,
                    -driver.right_stick_x
            ))
        }.schedule()

        driver.x.onTrue(
            InstantCommand {
                servo.position = 0.5
                Unit
            }
        )
        driver.y.onTrue(
            InstantCommand {
                servo.position = 1.0
                Unit
            }
        )

        Telemetry.justUpdate().schedule()

        Telemetry.addData("par1") { Drivetrain.encoders[0].delta }
        Telemetry.addData("perp") { Drivetrain.encoders[1].delta }
        Telemetry.addData("par2") { Drivetrain.encoders[2].delta }
        Telemetry.addLine         { Drivetrain.position.toString()  }
    }
}
