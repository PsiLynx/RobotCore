package org.ftc3825.subsystem

/*
import org.ftc3825.util.LeftExtendoServoName
import org.ftc3825.util.RightExtendoServoName
*/
import org.ftc3825.component.Component

object Extendo: Subsystem<Extendo> {
    override val components
        get() = arrayListOf<Component>()

    override fun update(deltaTime: Double) {
        /*
        setPosition(target)
        components.forEach { it.update(deltaTime) }
        */
    }
    /*
    val leftServo = Servo(LeftExtendoServoName)
    val rightServo = Servo(RightExtendoServoName)

    const val leftMax = 1.0
    const val leftMin = 0.0

    const val rightMax = 0.0
    const val rightMin = 1.0

    var target = 0.0


    fun setPosition(position: Double) {
        leftServo.position  = leftMin  + position * ( leftMax  - leftMin  )
        rightServo.position = rightMin + position * ( rightMax - rightMin )
    }

    fun extend()  { setPosition(1.0) }
    fun retract() { setPosition(1.0) }
    */
}
