package org.ftc3825.util

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.component.Motor
import org.ftc3825.component.Servo
import org.ftc3825.stateMachine.State
import org.ftc3825.stateMachine.StateMachine
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.component.Component

object Claw : Subsystem<Claw>, StateMachine {
    val claw = Servo("clawServo")
    override val components = arrayListOf<Component>()

    private var _state = StatesEnum.Unknown
    private var states = ArrayList<State>()
    private lateinit var statesMap: Map<StatesEnum, State>

    val state: State
        get() = statesMap[_state]!!

    init{

        states.add(Opened  { claw.position = 1.0 } )
        states.add(Closed  { claw.position = 0.0 } )
        states.add(Unknown {                     } )

        statesMap = mapOf(
            Pair(StatesEnum.Opened,  states[0]),
            Pair(StatesEnum.Closed,  states[1]),
            Pair(StatesEnum.Unknown, states[2]),
        )
    }

    override fun update(deltaTime: Double) { }
    override fun transitionTo(transition: Enum<*>) = state.transitionTo(transition)

    enum class Transition { Open, Close }
    enum class StatesEnum { Opened, Closed, Unknown }

    class Opened (override var execute: () -> Unit) : ClawState()
    class Closed (override var execute: () -> Unit) : ClawState()
    class Unknown(override var execute: () -> Unit) : ClawState()

    abstract class ClawState: State {
        override fun transitionTo(input: Enum<*>) {
            _state = when(input as Transition){
                Transition.Open -> StatesEnum.Opened
                Transition.Close -> StatesEnum.Closed
            }
            state.execute()
        }

    }
}
