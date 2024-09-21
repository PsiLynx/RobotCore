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
import org.ftc3825.util.Pose2D
import java.io.FileWriter

@TeleOp(name = "TELEOP", group = "a")
class Teleop: CommandOpMode() {

    var driver = Gamepad(gamepad1!!)
    var operator = Gamepad(gamepad2!!)

    override fun init() {
        Intake.init(hardwareMap)
        Extendo.init(hardwareMap)
        Drivetrain.init(hardwareMap)

        CommandScheduler.schedule(
            Drivetrain.run {
                it.setWeightedDrivePower(Pose2D(
                        driver.left_stick_x,
                        driver.left_stick_y,
                        driver.right_stick_x
                ))
            }
        )

        operator.dpad_up.whileTrue(   InstantCommand { Extendo.target += 0.05; Unit} )
        operator.dpad_down.whileTrue( InstantCommand { Extendo.target -= 0.05; Unit} )

        operator.left_bumper.onTrue(
            Intake.runOnce { it.retract() } parallelTo Extendo.runOnce { it.retract() }
        )
        operator.right_bumper.onTrue(
            (
                Extendo.runOnce { it.extend() }
                parallelTo WaitCommand(seconds = 1)
            ) andThen  Intake.runOnce { it.open() }
        )

        operator.x.whileTrue(
            Intake.run { it.intake() }
        )
        operator.y.whileTrue(
            Intake.run { it.outtake() }
        )

        initialize()

    }
}