package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.component.Motor
import org.ftc3825.component.Servo
import org.ftc3825.util.LeftExtendoServoName
import org.ftc3825.util.RightExtendoServoName
import org.ftc3825.util.centimeters
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.util.slideMotorName

object Extendo: Subsystem<Extendo> {
    override var initialized = false

    lateinit var leftServo: Servo
    lateinit var rightServo: Servo

    const val leftMax = 1.0
    const val leftMin = 0.0

    const val rightMax = 0.0
    const val rightMin = 1.0

    var target = 0.0

    override val motors
        get() = arrayListOf<Motor>()

    override fun init(hardwareMap: HardwareMap) {
        if(!initialized) {
            leftServo = Servo(LeftExtendoServoName, hardwareMap)
            rightServo = Servo(RightExtendoServoName, hardwareMap)

        }
        initialized = true
    }
    override fun update(deltaTime: Double) {
        setPosition(target)
        motors.forEach {
            it.update(deltaTime)
        }
    }

    fun setPosition(pos: Double) {
        leftServo.position  = leftMin  + pos * ( leftMax  - leftMin  )
        rightServo.position = rightMin + pos * ( rightMax - rightMin )
    }

    fun extend()  { setPosition(1.0) }
    fun retract() { setPosition(1.0) }
}