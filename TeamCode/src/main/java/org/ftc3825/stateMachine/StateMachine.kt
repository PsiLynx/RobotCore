package org.ftc3825.stateMachine

interface StateMachine {
    fun transitionTo(transition: Enum<*>)
}