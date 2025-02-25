package test

import org.ftc3825.command.internal.Command
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.sim.TestClass
import org.junit.Test

class  CommandInternalsTest: TestClass() {
    @Test fun testCommandScheduler(){
        var passing = false
        InstantCommand {passing = true  }.schedule()

        CommandScheduler.update()
        assert(passing)
    }
    @Test fun testRunCommand(){
        var counter = 0
        RunCommand {counter ++}.schedule()

        for( i in 0..<10){
            CommandScheduler.update()
        }

        assertEqual(counter, 10)
    }
    @Test fun testRaceWith(){
        commandTakes( loops = 10,
            WaitLoopsCommand(10) racesWith WaitLoopsCommand(20)
        )
    }
    @Test fun testCommandGroup(){
        commandTakes( loops = 30,
            WaitLoopsCommand(10) andThen WaitLoopsCommand(20)
        )
    }
    @Test fun testParallel(){
        commandTakes( loops = 20,
            WaitLoopsCommand(10) parallelTo WaitLoopsCommand(20)
        )
    }

    @Test fun testTimeout(){
        commandTakes( loops = 10,
            WaitLoopsCommand(10)
        )
    }

    private fun commandTakes(loops: Int, command: Command) {
        CommandScheduler.reset()

        CommandScheduler.schedule(command)
        var current = 0
        while(current < (loops + 10) && CommandScheduler.commands.contains(command)){
            CommandScheduler.update()
            println(CommandScheduler.commands)
            current ++
        }
        if(current == loops + 10){
            error("while overflowed (took 10 more than $loops loops and still not finished")
        }
        if(loops != current){
            error("took $current loops, should have finsished in $loops")
        }
    }
    private class WaitLoopsCommand(var loops: Int): Command(){
        override fun initialize() = println("initialized")
        override fun execute() {
            println(loops)
            loops --
        }
        override fun isFinished() = loops < 1
    }

}
