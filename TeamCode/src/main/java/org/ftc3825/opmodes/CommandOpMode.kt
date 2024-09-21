package org.ftc3825.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.util.Slides
import kotlin.random.Random

@Disabled
abstract class CommandOpMode: OpMode() {
    var rand = Random(0)

    fun initialize(){
        CommandScheduler.init(hardwareMap)
    }

    override fun loop() {
        CommandScheduler.update()
        telemetry.addData("acceleration", Slides.motor.acceleration )
        telemetry.addLine(CommandScheduler.status())
        telemetry.update()

    }

    override fun stop() {
        CommandScheduler.end()
    }
}
