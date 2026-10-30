package test

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.If
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class  CommandInternalsTest: TestClass() {
    @Test fun testIfCommand(){
        var passing = false
        CommandScheduler.reset()
        (
            If({false}, InstantCommand { passing = false } )
            .ElseIf({false}, InstantCommand { passing = false } )
            .ElseIf({true}, InstantCommand { passing = true})
            Else InstantCommand { passing = false }
        ).schedule()
        repeat(5) { CommandScheduler.update() }
        assert(passing)


    }
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
