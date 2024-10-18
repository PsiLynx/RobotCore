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

object Arm : Subsystem<Arm>() {
    override val motors = arrayListOf<Motor>()

    val pitchServo = Servo(armServoName)

    val minAngle = degrees(0)
    val maxAngle = degrees(90.0)

    override fun update(deltaTime: Double) { }

    fun pitchDown() = InstantCommand { pitchServo.position = 0.0; Unit}
    fun pitchUp() = InstantCommand { pitchServo.position = 1.0; Unit}

}
