package org.ftc3825.subsystem

import org.ftc3825.component.Component
import org.ftc3825.component.Servo
import org.ftc3825.util.armServoName

object Arm : Subsystem<Arm> {
    override val components = arrayListOf<Component>()

    val pitchServo = Servo(armServoName)

    override fun update(deltaTime: Double) { }

    fun pitchDown() {
        //pitchServo.position = 1.0
    }
    fun pitchUp() {
        //pitchServo.position = 0.0
    }

}
