package org.ftc3825.subsystem

import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.CRServo
import org.ftc3825.component.Component
import org.ftc3825.component.QuadratureEncoder
import org.ftc3825.component.Servo
import org.ftc3825.util.clawPitchMotorName
import org.ftc3825.util.flMotorName
import org.ftc3825.util.gripServoName
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.util.pitchServoName
import org.ftc3825.util.rollServoName
import kotlin.math.abs

object Claw : Subsystem<Claw> {

    val pitchServo = CRServo(pitchServoName)
    private val rollServo  = Servo(rollServoName)
    private val gripServo  = Servo(gripServoName)

    override val components = arrayListOf<Component>(
        pitchServo,
        rollServo,
        gripServo
    )

    private var pinched = false
    private const val pitchTPR = 8192.0

    val pitch
        get() = pitchServo.position

    init {
        pitchServo.encoder = QuadratureEncoder(clawPitchMotorName)
        pitchServo.initializeController(
            PIDFGParameters(
                P=0.0015,
                D=0.005
            )
        )
    }

    fun pitchUp() = setPitch(pitchTPR / 4)
    fun pitchDown() = setPitch(0.0)
    fun groundSpecimenPitch() = setPitch(pitchTPR / 12)

    /*
    fun pitchUp() = InstantCommand { pitchServo.position = 1.0}
    fun pitchDown() = InstantCommand { pitchServo.position = 0.0}
    fun groundSpecimenPitch() = InstantCommand { pitchServo.position = 0.3}
    */

    fun rollLeft() = InstantCommand { rollServo.position = 0.2 }
    fun rollCenter() = InstantCommand { rollServo.position = 0.48 }
    fun rollRight() = InstantCommand { rollServo.position = 0.8 }

    fun grab() = InstantCommand {
        gripServo.position = 0.7
        pinched = true
    }

    fun release() = InstantCommand {
        gripServo.position = 1.0
        pinched = false
    }

    fun toggleGrip() = InstantCommand {
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
        run {
            pitchServo.runToPosition(pitch)
            pitchServo.useFeedback = true
        }
//            until {
//                abs(pitchServo.error) < 10 && abs(pitchServo.encoder?.delta ?: 0.0) < 10
//            }
//            withEnd {
//                pitchServo.doNotFeedback()
//                pitchServo.power = 0.0
//            }
    )
    override fun update(deltaTime: Double) { }

    override fun reset() {
        components.forEach { it.reset() }
        pitchServo.position = 0.0
    }
}
