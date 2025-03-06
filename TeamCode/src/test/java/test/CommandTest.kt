package test

import org.firstinspires.ftc.teamcode.gvf.Line
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.fakehardware.FakeLocalizer
import org.firstinspires.ftc.teamcode.command.LogCommand
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.junit.Test
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Tangent

class CommandTest: TestClass() {
    private val localizer = FakeLocalizer(hardwareMap)
    @Test fun loggerTest(){
        val logCommand = LogCommand(Drivetrain)
        val path = Path(
            arrayListOf(
                Line(
                    0, 0,
                    10, 20,
                    Tangent()
                )
            )
        )

        ( logCommand racesWith FollowPathCommand(path) ).schedule()

        repeat(10){ CommandScheduler.update() }

    }
}
