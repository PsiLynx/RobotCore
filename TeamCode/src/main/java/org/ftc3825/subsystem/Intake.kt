package org.ftc3825.subsystem

import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Motor
import org.ftc3825.component.Servo
import org.ftc3825.component.CRServo
import org.ftc3825.stateMachine.State
import org.ftc3825.stateMachine.StateMachine
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.IntakeIntakeServoName
import org.ftc3825.util.IntakePivotServoName
import org.ftc3825.util.degrees
import org.ftc3825.command.internal.CommandScheduler

object Intake : Subsystem<Intake>() {
    override val motors = arrayListOf<Motor>()

    val pivotServo = Servo(IntakePivotServoName)
    val intakeServo = CRServo(IntakeIntakeServoName)

    val minAngle = degrees(0)
    val maxAngle = degrees(90.0)

    override fun update(deltaTime: Double) { }

    fun setAngle(angle: Double) {
        angle.coerceIn(minAngle, maxAngle)
        pivotServo.setAngle(minAngle + angle)

    }
    fun retract() = InstantCommand { setAngle(degrees(90.0)) }
    fun open()    = InstantCommand { setAngle(0.0)           }

    fun intake() = InstantCommand  { intakeServo.power =   1.0; Unit}
    fun outtake() = InstantCommand { intakeServo.power = - 1.0; Unit}
}
