package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.TimedCommand
import org.firstinspires.ftc.teamcode.command.internal.Trigger
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.nanoseconds
import org.junit.Test
import java.util.Random

class TriggerTest: TestClass() {

    @Test
    fun testAddTrigger(){
        var passing = false
        val rand = Random()
        rand.setSeed(1)

        val trigger = Trigger { rand.nextBoolean() }
        trigger.onTrue(
            InstantCommand { passing = true; return@InstantCommand Unit }
        )

        for (i in 1..5) {
            CommandScheduler.update()
        }
        assert(passing)
    }

//    @Test fun makeSureItsRepeatable(){
//        for (i in 1..1000) {
//            testAddTrigger()
//        }
//    }

    @Test
    fun testTriggerLifetimeReqs(){
        var passing = false
        val rand = Random()
        rand.setSeed(1)

        var trigger = Trigger { rand.nextBoolean() }
        trigger.onTrue(
            InstantCommand { passing = true; return@InstantCommand Unit }
        )

        trigger = Trigger { false }

        for (i in 1..5) {
            CommandScheduler.update()
        }
        assert(passing)
    }

    @Test
    fun testWhileTrue(){
        val start = Globals.timeSinceStart
        val trigger = Trigger {
            ( Globals.timeSinceStart - start ).toInt() % 2 == 0
        }

        var timedCommandStart = 0L
        trigger.whileTrue(
            TimedCommand(seconds=1) {
                assert(
                    nanoseconds(System.nanoTime() - timedCommandStart) < 2
                )
            } withInit {
                timedCommandStart = System.nanoTime()
                return@withInit Unit
            }
        )

        while (nanoseconds(System.nanoTime()) - start < 2){
            CommandScheduler.update()
        }
    }
}