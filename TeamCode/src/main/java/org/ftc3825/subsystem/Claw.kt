package org.ftc3825.subsystem

import org.ftc3825.component.CRServo
import org.ftc3825.component.Component
import org.ftc3825.component.QuadratureEncoder
import org.ftc3825.component.Servo
import org.ftc3825.util.flMotorName
import org.ftc3825.util.gripServoName
import org.ftc3825.util.pitchServoName
import org.ftc3825.util.rollServoName
import kotlin.math.abs

object Claw : Subsystem<Claw> {
    override val components = arrayListOf<Component>()

    private val pitchServo = CRServo(pitchServoName)
    private val rollServo = Servo(rollServoName)
    private val gripServo = Servo(gripServoName)

    var pinched = false
    val pitchTPR = 8192.0

    init {
        pitchServo.encoder = QuadratureEncoder(flMotorName)
    }

    override fun update(deltaTime: Double) { }


    fun pitchUp() = setPitch(0.0)
    fun pitchDown() = setPitch(pitchTPR / 4)
    fun groundSpecimenPitch() = setPitch(pitchTPR / 12)

    fun rollLeft() = runOnce { rollServo.position = 0.2 }
    fun rollCenter() = runOnce { rollServo.position = 0.48 }
    fun rollRight() = runOnce { rollServo.position = 0.8 }

    fun grab() = runOnce {
        gripServo.position = 0.7
        pinched = true
    }

    fun release() = runOnce {
        gripServo.position = 1.0
        pinched = false
    }

    fun toggleGrip() = runOnce {
        if(pinched) {
            gripServo.position = 1.0
            pinched = false
        }
        else{
            gripServo.position = 0.7
            pinched = true
        }
    }

    private fun setPitch(pitch: Double) = (
        run { pitchServo.runToPosition(pitch) }
            until { abs(pitchServo.error) < 10 }
            withEnd {
            pitchServo.doNotFeedback()
            pitchServo.power = 0.0
        }
        )
}
