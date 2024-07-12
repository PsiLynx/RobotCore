package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.GVF.Line
import org.firstinspires.ftc.teamcode.GVF.Path
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeLocalizer
import org.firstinspires.ftc.teamcode.sim.LogCommand
import org.firstinspires.ftc.teamcode.util.TestClass
import org.junit.Test

class CommandTest: TestClass() {
    var localizer = FakeLocalizer(hardwareMap)
    @Test
    fun loggerTest(){
        val path = Path(
            Line(
                0, 0,
                10, 20
            )
        )

        CommandScheduler.schedule(LogCommand())
        CommandScheduler.schedule(FollowPathCommand(localizer, path))

        for(i in 1..10){
            CommandScheduler.update()
        }
        CommandScheduler.end()
    }
}