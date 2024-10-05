package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.ftc3825.command.LogCommand
import org.ftc3825.command.RunMotorToPower
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.component.Gamepad
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.Extendo
import org.ftc3825.subsystem.Intake
import org.ftc3825.subsystem.ThreeDeadWheelLocalizer
import org.ftc3825.util.Pose2D
import java.io.FileWriter

@TeleOp(name = "TELEOP", group = "a")
class Teleop: CommandOpMode() {

    override fun init() {
        initialize()

        var driver = Gamepad(gamepad1!!)
        var operator = Gamepad(gamepad2!!)

        Drivetrain.init(hardwareMap)

        CommandScheduler.schedule(
            Drivetrain.run {
                it.setWeightedDrivePower(Pose2D(
                        driver.left_stick_y,
                        -driver.left_stick_x,
                        -driver.right_stick_x
                ))

            }
        )

        val localizer = ThreeDeadWheelLocalizer(
            Drivetrain.motors[0].motor,
            Drivetrain.motors[1].motor,
            Drivetrain.motors[2].motor
        )

        CommandScheduler.schedule(
            RunCommand {
                localizer.update()
                telemetry.addData("par1", localizer.par1.distance)
                telemetry.addData("par2", localizer.par2.distance)
                telemetry.addData("perp", localizer.perp.distance)
                telemetry.addLine(localizer.position.toString())
                telemetry.update()
            }
        )

    }
}