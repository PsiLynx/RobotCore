package org.firstinspires.ftc.teamcode.test

import org.ftc3825.GVF.Line
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.fakehardware.FakeLocalizer
import org.ftc3825.sim.LogCommand
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.TestClass
import org.junit.Test

class CommandTest: TestClass() {
    private val localizer = FakeLocalizer(hardwareMap)
    @Test fun loggerTest(){
        val command = LogCommand(Drivetrain)
        val path = org.ftc3825.GVF.Path(
            Line(
                0, 0,
                10, 20
            )
        )

        CommandScheduler.schedule(FollowPathCommand(localizer, path))
        CommandScheduler.schedule(command)

        for(i in 1..10){
            CommandScheduler.update()
        }
        CommandScheduler.commands.find { it == command }?.end(true)
    }
}