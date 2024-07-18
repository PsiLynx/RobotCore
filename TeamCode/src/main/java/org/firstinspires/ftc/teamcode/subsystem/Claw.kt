package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.component.Servo
import org.firstinspires.ftc.teamcode.stateMachine.State
import org.firstinspires.ftc.teamcode.stateMachine.StateMachine



object Claw : Subsystem, StateMachine {
    override var initialized = false
    private lateinit var claw: Servo
    override val motors = arrayListOf<Motor>()

    private var _state = StatesEnum.Unknown
    private var states = ArrayList<State>()
    private lateinit var statesMap: Map<StatesEnum, State>

    val state: State
        get() = statesMap[_state]!!

    override fun init(hardwareMap: HardwareMap) {
        if(!initialized) {
            claw = Servo("clawServo", hardwareMap)

            states.add(Opened  { claw.position = 1.0 } )
            states.add(Closed  { claw.position = 0.0 } )
            states.add(Unknown {                     } )

            statesMap = mapOf(
                Pair(StatesEnum.Opened,  states[0]),
                Pair(StatesEnum.Closed,  states[1]),
                Pair(StatesEnum.Unknown, states[2]),
            )
        }
        initialized = true
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
                Transition.Open ->  StatesEnum.Opened
                Transition.Close -> StatesEnum.Closed
            }
            state.execute()
        }

    }
}
