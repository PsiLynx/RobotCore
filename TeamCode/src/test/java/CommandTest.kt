package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.GVF.Line
import org.firstinspires.ftc.teamcode.GVF.Path
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeLocalizer
import org.firstinspires.ftc.teamcode.sim.LogCommand
import org.junit.Test

class CommandTest {
    var hardwareMap = FakeHardwareMap()
    var localizer = FakeLocalizer(hardwareMap)
    @Test
    fun loggerTest(){
        CommandScheduler.init(hardwareMap)
        var path = Path(
            Line(
                0, 0,
                10, 20
            )
        )

        CommandScheduler.schedule(LogCommand())
        CommandScheduler.schedule(FollowPathCommand(localizer, path))

        for(i in 1..10){
            CommandScheduler.update()
            hardwareMap.updateDevices()

            //println(localizer.position)
        }
        CommandScheduler.end()
    }
}