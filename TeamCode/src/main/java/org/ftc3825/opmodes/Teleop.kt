package org.ftc3825.opmodes


import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.LocalizerSubsystem
import org.ftc3825.util.Pose2D

@TeleOp(name = "TELEOP", group = "a")
class Teleop: CommandOpMode() {

    override fun init() {
        initialize()

        var driver = Gamepad(gamepad1!!)
        var operator = Gamepad(gamepad2!!)

        Drivetrain.init(hardwareMap)
        LocalizerSubsystem.init(hardwareMap)

        CommandScheduler.schedule(
            Drivetrain.run {
                it.setWeightedDrivePower(Pose2D(
                        driver.left_stick_y,
                        -driver.left_stick_x,
                        -driver.right_stick_x
                ))

            }
        )

        CommandScheduler.schedule(
            RunCommand {
                LocalizerSubsystem.update()
                telemetry.addData("par1", LocalizerSubsystem.encoders[0].distance)
                telemetry.addData("perp", LocalizerSubsystem.encoders[1].distance)
                telemetry.addData("par2", LocalizerSubsystem.encoders[2].distance)
                telemetry.addLine(LocalizerSubsystem.position.toString())
                telemetry.update()
            }
        )

    }
}