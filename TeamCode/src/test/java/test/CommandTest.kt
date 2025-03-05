package test

import org.teamcode.gvf.Line
import org.teamcode.gvf.Path
import org.teamcode.command.FollowPathCommand
import org.teamcode.command.internal.CommandScheduler
import org.teamcode.fakehardware.FakeLocalizer
import org.teamcode.command.LogCommand
import org.teamcode.subsystem.Drivetrain
import org.teamcode.sim.TestClass
import org.junit.Test
import org.teamcode.gvf.HeadingType.Tangent

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
