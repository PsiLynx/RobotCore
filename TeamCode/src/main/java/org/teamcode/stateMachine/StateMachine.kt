package org.teamcode.stateMachine

interface StateMachine {
    fun transitionTo(transition: Enum<*>)
}