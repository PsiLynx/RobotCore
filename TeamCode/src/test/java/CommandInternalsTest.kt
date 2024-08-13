package org.firstinspires.ftc.teamcode.test
import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.command.internal.TimedCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.sim.timeStep
import org.ftc3825.util.TestClass
import org.ftc3825.util.assertEqual
import org.ftc3825.util.assertWithin
import org.junit.Test

class CommandInternalsTest: TestClass() {
    @Test fun testCommandScheduler(){
        var passing = false
        CommandScheduler.schedule(InstantCommand {passing = true; return@InstantCommand Unit })

        CommandScheduler.update()
        assert(passing)
    }
    @Test fun testRunCommand(){
        var counter = 0
        CommandScheduler.schedule(RunCommand {counter ++})

        for( i in 0..<10){
            CommandScheduler.update()
        }

        assertEqual(counter, 10)
    }
    @Test fun testWaitCommand(){
        commandTakes( seconds = 1,
            WaitCommand(1)
        )
    }
    @Test fun testRaceWith(){
        commandTakes( seconds = 1,
            WaitCommand(1) racesWith WaitCommand(2)
        )
    }
    @Test fun testCommandGroup(){
        commandTakes( seconds = 3,
            WaitCommand(1) andThen WaitCommand(2)
        )
    }
    @Test fun testParallel(){
        commandTakes( seconds = 2,
            WaitCommand(1) parallelTo WaitCommand(2)
        )
    }

    @Test fun testTimeout(){
        commandTakes( seconds = 1,
            TimedCommand(
                seconds = 1,
                RunCommand { }
            )
        )
    }

    private fun commandTakes(seconds: Number, command: Command) {
        CommandScheduler.schedule(
            command
        )
        val loops = seconds.toDouble() / timeStep
        var current = 0
            while (command in CommandScheduler.commands) {
                CommandScheduler.update()
                current ++
            }
        assertWithin(current - loops, epsilon=5)
    }

}