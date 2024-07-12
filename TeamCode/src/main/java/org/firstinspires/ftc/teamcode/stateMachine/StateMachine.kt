package org.firstinspires.ftc.teamcode.stateMachine

interface StateMachine {
    enum class transitions
    companion object {
        lateinit var state: State
    }

    //var states:ArrayList<State>
    fun transitionTo(transition: Enum<*>)
}