package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.TimedCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.junit.Test

class CommandSystemTest {
    var hardwareMap = FakeHardwareMap()
    init{
        CommandScheduler.init(hardwareMap)
    }
    @Test
    fun testCommandScheduler(){
        var passing = false
        CommandScheduler.schedule(InstantCommand {passing = true; return@InstantCommand Unit })

        CommandScheduler.update()
        assert(passing)
    }

    @Test
    fun testRunCommand(){
        var counter = 0
        CommandScheduler.schedule(RunCommand {counter ++})

        for( i in 0..<10){
            CommandScheduler.update()
        }

        assertEqual(counter, 10)
    }

    @Test
    fun testWaitCommand(){
        commandTakes( seconds = 1,
            WaitCommand(1)
        )
    }

    @Test
    fun testRaceWith(){
        commandTakes( seconds = 1,
            WaitCommand(1) racesWith WaitCommand(2)
        )
    }

    @Test
    fun testCommandGroup(){
        commandTakes( seconds = 3,
            WaitCommand(1) andThen WaitCommand(2)
        )
    }

    @Test
    fun testParallel(){
        commandTakes( seconds = 2,
            WaitCommand(1) parallelTo WaitCommand(2)
        )
    }

    @Test
    fun testTimeout(){
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

        assertTakes(seconds = seconds) {
            while (command in CommandScheduler.commands) {
                CommandScheduler.update()
            }
        }
    }

}