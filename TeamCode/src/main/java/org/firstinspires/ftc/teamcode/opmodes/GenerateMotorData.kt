package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.command.LogCommand
import org.firstinspires.ftc.teamcode.command.RunMotorToPower
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.IOComponent
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.hardware.HWQue
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.Subsystem
import java.io.FileWriter

@Autonomous(name = "generate hardwareDevice data", group = "utils")
@Disabled
class GenerateMotorData: CommandOpMode() {
    val Sub = object: Subsystem<Subsystem.DummySubsystem>() {
        val motor = HWQue.managed(Motor(
            "test",
            HardwareMap.DeviceTimes.chubMotor,
            Component.Direction.FORWARD,
            1.0,
            1.0
        ))

        override val components: List<IOComponent> = arrayListOf<IOComponent>(motor)

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
