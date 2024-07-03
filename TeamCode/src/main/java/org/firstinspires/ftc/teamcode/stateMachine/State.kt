package org.firstinspires.ftc.teamcode.stateMachine

interface State {
    enum class transition()

    fun transitionTo(input: Enum<*>)
    var execute:() -> Unit


}