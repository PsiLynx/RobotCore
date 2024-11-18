package org.ftc3825.subsystem

import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Motor
import org.ftc3825.component.Servo
import org.ftc3825.component.CRServo
import org.ftc3825.stateMachine.State
import org.ftc3825.stateMachine.StateMachine
import org.ftc3825.util.armServoName
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.degrees
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.component.Component

object Arm : Subsystem<Arm>() {
    override val components = arrayListOf<Component>()

    val pitchServo = Servo(armServoName)

    override fun update(deltaTime: Double) { }

    fun pitchDown() {
        pitchServo.position = 1.0
        Unit
    }
    fun pitchUp() {
        pitchServo.position = 0.0
        Unit
    }

}
