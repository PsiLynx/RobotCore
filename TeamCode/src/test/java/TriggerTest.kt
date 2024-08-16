package org.firstinspires.ftc.teamcode.test

import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.command.internal.Trigger
import org.ftc3825.component.Gamepad
import org.ftc3825.fakehardware.FakeGamepad
import org.ftc3825.util.TestClass
import org.junit.Test
import java.util.Random
import org.ftc3825.util.unit

class TriggerTest: TestClass() {

    @Test fun testAddTrigger(){
        var passing = false
        val rand = Random()
        rand.setSeed(1)

        val trigger = Trigger { rand.nextBoolean() }
        trigger.onTrue(
            InstantCommand { passing = true; Unit }
        )

        for (i in 1..5) {
            CommandScheduler.update()
        }
        assert(passing)
    }
    @Test fun testTriggerLifetime(){
        var passing = false
        val rand = Random()
        rand.setSeed(1)

        var trigger = Trigger { rand.nextBoolean() }
        trigger.onTrue(
            InstantCommand { passing = true; Unit }
        )

        trigger = Trigger { false } // change the trigger to never pass

        repeat(5) { CommandScheduler.update() }

        assert(passing)
    }
    @Test fun testWhileTrue(){
        val gamepad = Gamepad("trigger test gamepad", hardwareMap)
        val trigger = gamepad.dpad_up

        trigger.whileTrue(
            RunCommand {
                assert(gamepad.gamepad.dpad_up == true)
            } withEnd { _ ->
                assert(gamepad.gamepad.dpad_up == false)
            }
        )

        (gamepad.gamepad as FakeGamepad).press("dpad_up")
        repeat(10) { CommandScheduler.update() }

        (gamepad.gamepad as FakeGamepad).depress("dpad_up")
        println(gamepad.gamepad.dpad_up)
        CommandScheduler.update()
    }

    @Test fun testAnd(){
        var pass = false
        val gamepad = Gamepad("trigger test gamepad", hardwareMap)
        (gamepad.x and gamepad.y).whileTrue(
            RunCommand { pass = true; Unit }
        )

        CommandScheduler.update()
        assert(!pass)

        (gamepad.gamepad as FakeGamepad).press("x")
        CommandScheduler.update()
        assert(!pass)

        (gamepad.gamepad as FakeGamepad).press("y")
        CommandScheduler.update()
        assert(pass)

    }

}