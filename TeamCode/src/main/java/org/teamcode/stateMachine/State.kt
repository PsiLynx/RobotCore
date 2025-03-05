package org.teamcode.stateMachine

interface State {
    fun transitionTo(input: Enum<*>)
    var execute:() -> Unit


}