package test

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.If
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.subsystem.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.SubsystemGroup
import org.junit.Test

class  CommandInternalsTest: TestClass() {
    @Test fun testSubsystemComp(){
        open class emptySubsystem: Subsystem<Subsystem.DummySubsystem> {
            override val components = listOf<Component>()
            override fun update(deltaTime: Double) { }
        }
        val sub1 = object: emptySubsystem() {}
        val sub2 = object: emptySubsystem() {}
        val sub3 = object: emptySubsystem() {}
        val sub4 = object: emptySubsystem() {}
        val sub5 = object: emptySubsystem() {}

        val comp1 = object: SubsystemGroup<Subsystem.DummySubsystem>(sub1, sub2){
            override fun update(deltaTime: Double) {}
        }
        val comp2 = object: SubsystemGroup<Subsystem.DummySubsystem>(sub3, sub4){
            override fun update(deltaTime: Double) {}
        }
        val comp3 = object: SubsystemGroup<Subsystem.DummySubsystem>(
            sub3, sub4, sub1
        ){
            override fun update(deltaTime: Double) {}
        }
        val comp4 = object: SubsystemGroup<Subsystem.DummySubsystem>(
            comp1
        ){
            override fun update(deltaTime: Double) {}
        }
        assert(sub1.conflictsWith(sub1) == true )
        assert(sub1.conflictsWith(sub2) == false )
        assert(comp1.conflictsWith(sub1) == true )
        assert(comp1.conflictsWith(comp2) == false)
        assert(comp1.conflictsWith(comp3) == true )
        assert(comp4.conflictsWith(sub1) == true )
        assert(comp4.conflictsWith(comp3) == true )
    }
    @Test fun testIfCommand(){
        var passing = false
        CommandScheduler.reset()
        (
            If({false}, InstantCommand { passing = false } )
            .elseIf({false}, InstantCommand { passing = false } )
            .elseIf({true}, InstantCommand { passing = true})
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
