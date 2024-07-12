package org.firstinspires.ftc.teamcode.stateMachine

interface StateMachine {
    fun transitionTo(transition: Enum<*>)
}