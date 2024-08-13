package org.firstinspires.ftc.teamcode.test

import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.TimedCommand
import org.ftc3825.command.internal.Trigger
import org.ftc3825.util.Globals
import org.ftc3825.util.TestClass
import org.ftc3825.util.nanoseconds
import org.junit.Test
import java.util.Random

class TriggerTest: TestClass() {

    @Test fun testAddTrigger(){
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
    @Test fun testTriggerLifetimeReqs(){
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
    @Test fun testWhileTrue(){
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