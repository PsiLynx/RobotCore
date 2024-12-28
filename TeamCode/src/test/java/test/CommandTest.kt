package test

import org.ftc3825.gvf.Line
import org.ftc3825.gvf.Path
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.fakehardware.FakeLocalizer
import org.ftc3825.command.LogCommand
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.TestClass
import org.junit.Test
import org.ftc3825.gvf.HeadingType.Tangent

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
