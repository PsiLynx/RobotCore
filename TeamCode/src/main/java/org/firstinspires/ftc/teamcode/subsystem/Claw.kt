package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.stateMachine.State
import org.firstinspires.ftc.teamcode.stateMachine.StateMachine



object Claw : Subsystem, StateMachine {
    private lateinit var claw: Servo

    var states = ArrayList<State>()
    private lateinit var statesMap: Map<statesEnum, State>

    private var _state: statesEnum = statesEnum.unkown
    val state: State
        get() = statesMap[_state]!!

    override fun init(hardwareMap: HardwareMap) {
        claw = Servo("clawServo", hardwareMap)

        states.add( opened  { claw.position = 1.0 } )
        states.add( closed  { claw.position = 0.0 } )
        states.add( unknown {                     } )

        statesMap = mapOf<statesEnum, State>(
            Pair(statesEnum.opened, states[0]),
            Pair(statesEnum.closed, states[1]),
            Pair(statesEnum.unkown, states[2]),
        )

        print("")
    }

    enum class transition() {
        open, close
    }
    enum class statesEnum() {
        opened, closed, unkown
    }
    abstract class ClawState: State {
        override fun transitionTo(input: Enum<*>) {
            _state = when(input as transition){
                transition.open ->  statesEnum.opened
                transition.close -> statesEnum.closed
            }
            state.execute()
        }

    }
    class opened (override var execute: () -> Unit) : ClawState() { }
    class closed (override var execute: () -> Unit) : ClawState() { }
    class unknown(override var execute: () -> Unit) : ClawState() { }

    override fun transitionTo(transition: Enum<*>) = state.transitionTo(transition)
}
