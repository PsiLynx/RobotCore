package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.ftc3825.command.LogCommand
import org.ftc3825.command.RunMotorToPower
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.util.Slides
import java.io.FileWriter

@Autonomous(name = "generate hardwareDevice data", group = "utils")
@Disabled
class GenerateMotorData: CommandOpMode() {
    private val logCommand = LogCommand(Slides)

    override fun initialize() {

        val text = "test"
        val startDate = "test"
        val path = "/sdcard/FIRST/userLogs/$startDate.json"

        FileWriter(path, false).write(
            text.toCharArray()
        )

        val dataGeneratorCommand: Command = (
                        RunMotorToPower( 1.0, Slides, Slides.motor)
                andThen RunMotorToPower(-1.0, Slides, Slides.motor)
                andThen RunMotorToPower( 0.8, Slides, Slides.motor)
                andThen RunMotorToPower(-0.8, Slides, Slides.motor)
                andThen RunMotorToPower( 0.6, Slides, Slides.motor)
                andThen RunMotorToPower(-0.6, Slides, Slides.motor)
                andThen RunMotorToPower( 0.4, Slides, Slides.motor)
                andThen RunMotorToPower(-0.4, Slides, Slides.motor)
                andThen RunMotorToPower( 0.2, Slides, Slides.motor)
                andThen RunMotorToPower(-0.2, Slides, Slides.motor)
                andThen RunMotorToPower( 0.1, Slides, Slides.motor)
                andThen RunMotorToPower(-0.1, Slides, Slides.motor)
                andThen InstantCommand { CommandScheduler.end(logCommand) }

            )

        (dataGeneratorCommand racesWith logCommand).schedule()
        RunCommand {
            telemetry.addData("acceleration", Slides.motor.acceleration )
            telemetry.addLine(CommandScheduler.status())
            telemetry.update()
        }.schedule()
    }
}
