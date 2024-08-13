package org.ftc3825.stateMachine

interface State {
    fun transitionTo(input: Enum<*>)
    var execute:() -> Unit


}