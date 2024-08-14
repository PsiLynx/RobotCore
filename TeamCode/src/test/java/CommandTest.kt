package org.firstinspires.ftc.teamcode.test

import org.ftc3825.GVF.Line
import org.ftc3825.GVF.Path
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.fakehardware.FakeLocalizer
import org.ftc3825.command.LogCommand
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.TestClass
import org.junit.Test

class CommandTest: TestClass() {
    private val localizer = FakeLocalizer(hardwareMap)
    @Test fun loggerTest(){
        val logCommand = LogCommand(Drivetrain)
        val path = Path(
            Line(
                0, 0,
                10, 20
            )
        )

        CommandScheduler.schedule(logCommand)
        CommandScheduler.schedule(FollowPathCommand(localizer, path))

        repeat(10){ CommandScheduler.update() }

        CommandScheduler.commands.find { it == logCommand }?.end(true)
    }
}