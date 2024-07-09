package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.GVF.Line
import org.firstinspires.ftc.teamcode.GVF.Path
import org.firstinspires.ftc.teamcode.command.CommandScheduler
import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeLocalizer
import org.firstinspires.ftc.teamcode.sim.LogCommand
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.junit.Test

class CommandTest {
    var hardwareMap = FakeHardwareMap()
    var scheduler = CommandScheduler(hardwareMap)
    var localizer = FakeLocalizer(hardwareMap)
    @Test
    fun loggerTest(){
        var path = Path(
            Line(
                0, 0,
                10, 10
            )
        )

        scheduler.schedule(LogCommand())
        scheduler.schedule(FollowPathCommand(localizer, path))

        for(i in 1..1){
            scheduler.update()
            hardwareMap.updateDevices()

            //println(localizer.position)
        }
        scheduler.end()
    }
}