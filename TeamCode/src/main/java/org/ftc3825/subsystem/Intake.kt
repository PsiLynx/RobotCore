package org.ftc3825.subsystem

/*
import org.ftc3825.util.IntakeIntakeServoName
import org.ftc3825.util.IntakePivotServoName
*/
import org.ftc3825.component.Component

object Intake : Subsystem<Intake> {
    override val components = arrayListOf<Component>()

    override fun update(deltaTime: Double) { }

    /*
    val pivotServo = Servo(IntakePivotServoName)
    val intakeServo = CRServo(IntakeIntakeServoName)

    val minAngle = degrees(0)
    val maxAngle = degrees(90.0)


    fun setAngle(angle: Double) {
        angle.coerceIn(minAngle, maxAngle)
        pivotServo.setAngle(minAngle + angle)

    }
    fun retract() = InstantCommand { setAngle(degrees(90.0)) }
    fun open()    = InstantCommand { setAngle(0.0)           }

    fun intake() = InstantCommand  { intakeServo.power =   1.0}
    fun outtake() = InstantCommand { intakeServo.power = - 1.0}
    */

}
