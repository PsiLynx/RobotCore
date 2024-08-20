package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.ftc3825.command.LogCommand
import org.ftc3825.command.RunMotorToPower
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.subsystem.Slides

@Autonomous(name = "generate motor data", group = "utils")
class GenerateMotorData: CommandOpMode() {
    private val logCommand = LogCommand(Slides)

    override fun init() {
        Slides.init(hardwareMap)

        var dataGeneratorCommand: Command = (
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

            )
        // dataGeneratorCommand = dataGeneratorCommand.withEnd { Slides.motor.setPower(0.0) }

        initialize()

        CommandScheduler.schedule(dataGeneratorCommand)
        CommandScheduler.schedule(logCommand)
    }
}