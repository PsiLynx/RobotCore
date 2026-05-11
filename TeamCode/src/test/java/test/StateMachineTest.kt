package test

import junit.framework.TestCase.*
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.StateMachine
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class StateMachineTest: TestClass() {
    private class FakeCommand(
        private val finishCondition: () -> Boolean = { false }
    ) : Command(name = { "fake" }) {

        var initializeCount = 0
        var executeCount = 0
        var endCount = 0
        var interrupted = false
        var scheduled = false

        override fun initialize() {
            initializeCount++
            scheduled = true
        }

        override fun execute() {
            executeCount++
        }

        override fun end(interrupted: Boolean) {
            endCount++
            this.interrupted = interrupted
            scheduled = false
        }

        override fun isFinished(): Boolean = finishCondition()
    }

    @Test
    fun `state machine walks through branching transitions and terminates correctly`() {

        var allowAToB = false
        var allowBToC = false
        var finishB = false
        var finishC = false

        val commandA = FakeCommand()
        val commandB = FakeCommand { finishB }
        val commandC = FakeCommand { finishC }

        val machine = StateMachine("test")

        val stateA = machine.addState(commandA)
        val stateB = machine.addState(commandB)
        val stateC = machine.addState(commandC)

        stateA switchTo stateB after { allowAToB }
        stateB switchTo stateC afterCompletedAnd { allowBToC }

        machine.initialize()

        // initial state entered
        assertTrue(commandA.scheduled)
        assertFalse(commandB.scheduled)
        assertFalse(commandC.scheduled)
        assertEquals(stateA, machine.currentState)

        // no transition yet
        machine.execute()

        assertEquals(stateA, machine.currentState)
        assertTrue(commandA.scheduled)

        // trigger A -> B
        allowAToB = true
        machine.execute()

        assertEquals(stateB, machine.currentState)

        assertFalse(commandA.scheduled)
        assertTrue(commandB.scheduled)
        assertFalse(commandC.scheduled)

        // B should not transition until both conditions true
        machine.execute()

        assertEquals(stateB, machine.currentState)

        allowBToC = true
        machine.execute()

        assertEquals(stateB, machine.currentState)

        finishB = true
        machine.execute()

        assertEquals(stateC, machine.currentState)
        assertFalse(commandB.scheduled)
        assertTrue(commandC.scheduled)

        finishC = true
        machine.execute()


        // explicit finish
        assertFalse(machine.isFinished())

        assertTrue(machine.changeState(null))

        assertTrue(machine.isFinished())
    }

    @Test
    fun `afterCompletedOr transitions when either condition becomes true`() {

        var completed = false
        var externalCondition = false

        val first = FakeCommand { completed }
        val second = FakeCommand()

        val machine = StateMachine("or-test")

        val state1 = machine.addState(first)
        val state2 = machine.addState(second)

        (state1 switchTo state2) afterCompletedOr { externalCondition }

        machine.initialize()

        // neither condition true
        machine.execute()

        assertEquals(state1, machine.currentState)

        // external condition alone should trigger
        externalCondition = true
        machine.execute()

        assertEquals(state2, machine.currentState)
    }

    @Test
    fun `transition priority uses first matching transition`() {

        var firstCondition = false
        var secondCondition = false

        val start = FakeCommand()
        val left = FakeCommand()
        val right = FakeCommand()

        val machine = StateMachine("priority")

        val startState = machine.addState(start)
        val leftState = machine.addState(left)
        val rightState = machine.addState(right)

        (startState switchTo leftState) after { firstCondition }
        (startState switchTo rightState) after { secondCondition }

        machine.initialize()

        firstCondition = true
        secondCondition = true

        machine.execute()

        // first transition added should win
        assertEquals(leftState, machine.currentState)

        assertTrue(left.scheduled)
        assertFalse(right.scheduled)
    }

    @Test
    fun `manual state changes correctly call exit and enter hooks`() {

        val first = FakeCommand()
        val second = FakeCommand()

        val machine = StateMachine("manual")

        val state1 = machine.addState(first)
        val state2 = machine.addState(second)

        machine.initialize()

        assertTrue(first.scheduled)
        assertFalse(second.scheduled)

        val changed = machine.changeState(state2)

        assertTrue(changed)

        assertFalse(first.scheduled)
        assertTrue(second.scheduled)

        assertEquals(1, first.endCount)
        assertEquals(state2, machine.currentState)

        machine.end(false)

        assertFalse(second.scheduled)
        assertEquals(1, second.endCount)
    }

    @Test
    fun `invalid state transition does not mutate machine`() {

        val machine = StateMachine("invalid")

        val valid = machine.addState(FakeCommand())

        val otherMachine = StateMachine("other")
        val foreign = otherMachine.addState(FakeCommand())

        machine.initialize()

        val changed = machine.changeState(foreign)

        assertFalse(changed)
        assertEquals(valid, machine.currentState)
    }

    @Test
    fun `collection interface reflects backing state list`() {

        val machine = StateMachine("collection")

        val a = machine.addState(FakeCommand())
        val b = machine.addState(FakeCommand())

        assertEquals(2, machine.size)
        assertTrue(machine.contains(a))
        assertTrue(machine.contains(b))
        assertFalse(machine.isEmpty())

        val iterated = machine.toList()

        assertEquals(listOf(a, b), iterated)
    }
}