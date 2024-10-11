package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Localizer
import org.ftc3825.subsystem.TelemetrySubsystem
import org.ftc3825.util.Pose2D

@TeleOp(name = "TELEOP", group = "a")
class Teleop: CommandOpMode() {

    override fun init() {
        initialize()
        TelemetrySubsystem.telemetry = telemetry

        var driver = Gamepad(gamepad1!!)
        var operator = Gamepad(gamepad2!!)

        Drivetrain.init(hardwareMap)
        Localizer.init(hardwareMap)

        Drivetrain.run {
            it.setWeightedDrivePower(Pose2D(
                    driver.left_stick_y,
                    -driver.left_stick_x,
                    -driver.right_stick_x
            ))
        }.schedule()

        Localizer.justUpdate().schedule()
        TelemetrySubsystem.justUpdate().schedule()

        TelemetrySubsystem.addData("par1") { Localizer.encoders[0].distance }
        TelemetrySubsystem.addData("perp") { Localizer.encoders[1].distance }
        TelemetrySubsystem.addData("par2") { Localizer.encoders[2].distance }
        TelemetrySubsystem.addLine         { Localizer.position.toString()  }
    }
}
