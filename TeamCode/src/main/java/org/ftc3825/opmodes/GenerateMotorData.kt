package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.ftc3825.command.LogCommand
import org.ftc3825.command.RunMotorToPower
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.component.Component
import org.ftc3825.component.Motor
import org.ftc3825.subsystem.Subsystem
import java.io.FileWriter

@Autonomous(name = "generate hardwareDevice data", group = "utils")
@Disabled
class GenerateMotorData: CommandOpMode() {
    val Sub = object: Subsystem<Subsystem.DummySubsystem> {
        val motor = Motor("test", 435, Component.Direction.FORWARD)

        override val components = arrayListOf<Component>(motor)

        override fun update(deltaTime: Double) { }

    }
    private val logCommand = LogCommand(Sub)

    override fun initialize() {

        val text = "test"
        val startDate = "test"
        val path = "/sdcard/FIRST/userLogs/$startDate.json"

        FileWriter(path, false).write(
            text.toCharArray()
        )

        val dataGeneratorCommand: Command = (
                        RunMotorToPower( 1.0, Sub, Sub.motors[0])
                andThen RunMotorToPower(-1.0, Sub, Sub.motors[0])
                andThen RunMotorToPower( 0.8, Sub, Sub.motors[0])
                andThen RunMotorToPower(-0.8, Sub, Sub.motors[0])
                andThen RunMotorToPower( 0.6, Sub, Sub.motors[0])
                andThen RunMotorToPower(-0.6, Sub, Sub.motors[0])
                andThen RunMotorToPower( 0.4, Sub, Sub.motors[0])
                andThen RunMotorToPower(-0.4, Sub, Sub.motors[0])
                andThen RunMotorToPower( 0.2, Sub, Sub.motors[0])
                andThen RunMotorToPower(-0.2, Sub, Sub.motors[0])
                andThen RunMotorToPower( 0.1, Sub, Sub.motors[0])
                andThen RunMotorToPower(-0.1, Sub, Sub.motors[0])
                andThen InstantCommand { CommandScheduler.end(logCommand) }

            )

        (dataGeneratorCommand racesWith logCommand).schedule()
        RunCommand {
            telemetry.addData("acceleration", Sub.motors[0].acceleration )
            telemetry.addLine(CommandScheduler.status())
            telemetry.update()
        }.schedule()
    }
}
