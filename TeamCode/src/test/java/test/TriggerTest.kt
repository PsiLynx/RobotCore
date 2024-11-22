package org.ftc3825.test.test

import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.command.internal.Trigger
import org.ftc3825.component.Gamepad
import org.ftc3825.fakehardware.FakeGamepad
import org.ftc3825.util.TestClass
import org.junit.Test
import java.util.Random

class TriggerTest: TestClass() {

    @Test fun testAddTrigger(){
        var passing = false
        val rand = Random()
        rand.setSeed(1)

        val trigger = Trigger { rand.nextBoolean() }
        trigger.onTrue(
            InstantCommand { passing = true }
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
            InstantCommand { passing = true }
        )

        trigger = Trigger { false } // change the trigger to never pass

        repeat(5) { CommandScheduler.update() }

        assert(passing)
    }
    @Test fun testWhileTrue(){
        val gamepad = Gamepad("trigger test gamepad")
        val trigger = gamepad.dpadUp

        var pressed = false

        trigger.whileTrue(
            RunCommand {
                pressed = true
            } withEnd { _ ->
                pressed = false
            }
        )

        (gamepad.gamepad as FakeGamepad).press("dpad_up")
        CommandScheduler.update()
        assert(pressed)

        (gamepad.gamepad as FakeGamepad).depress("dpad_up")
        CommandScheduler.update()
        assert(!pressed)
    }

    @Test fun testAnd(){
        var pass = false
        val gamepad = Gamepad("trigger test gamepad")
        (gamepad.x and gamepad.y).whileTrue(
            RunCommand { pass = true }
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

    @Test fun testOnTrue() {
        val gamepad = Gamepad("onTrue test gamepad")
        val trigger = gamepad.dpadUp

        var passing = false

        trigger.onTrue(
            RunCommand {
                passing = true
            } withEnd { _ ->
                passing = false
            }
        )

        (gamepad.gamepad as FakeGamepad).press("dpad_up")
        CommandScheduler.update()
        assert(passing)

        (gamepad.gamepad as FakeGamepad).depress("dpad_up")
        CommandScheduler.update()
        assert(passing)
    }
}
