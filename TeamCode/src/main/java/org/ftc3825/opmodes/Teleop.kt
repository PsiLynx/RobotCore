package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Pose2D

@TeleOp(name = "TELEOP", group = "a")
class Teleop: CommandOpMode() {

    override fun init() {
        initialize()

        Telemetry.telemetry = telemetry

        var driver = Gamepad(gamepad1!!)
        var operator = Gamepad(gamepad2!!)

//        Drivetrain.run {
//            it.setWeightedDrivePower(Pose2D(
//                    driver.left_stick_y,
//                    -driver.left_stick_x,
//                    -driver.right_stick_x
//            ))
//        }.schedule()

        OuttakeSlides.run {
            it.setPower(
                operator.left_stick_y.toDouble()
            )
        }.schedule()

        Telemetry.justUpdate().schedule()

//        Telemetry.addData("par1") { Drivetrain.encoders[0].delta }
//        Telemetry.addData("perp") { Drivetrain.encoders[1].delta }
//        Telemetry.addData("par2") { Drivetrain.encoders[2].delta }
        Telemetry.addData("power") { OuttakeSlides.leftMotor.lastWrite?:0 }
//        Telemetry.addLine         { Drivetrain.position.toString()  }
    }
}
