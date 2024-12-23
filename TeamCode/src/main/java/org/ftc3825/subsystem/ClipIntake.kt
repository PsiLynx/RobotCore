package org.ftc3825.subsystem

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.ftc3825.component.Servo
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component
import org.ftc3825.cv.GamePiecePipeLine
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Vector2D
import org.ftc3825.util.degrees
import org.ftc3825.util.intakeGripServoName
import org.ftc3825.util.intakeRollServoName
import org.ftc3825.util.intakePitchServoName
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation

object ClipIntake : Subsystem<OuttakeClaw> {

    var pinched = false
    private val pitchServo = Servo(intakePitchServoName)
    private val flipServo = Servo(intakeRollServoName)
    private val gripServo = Servo(intakeGripServoName)

    override val components = arrayListOf<Component>(pitchServo, flipServo, gripServo)

    override fun update(deltaTime: Double) { }

    fun pitchLeft() { pitchServo.position = 1.0 }
    fun pitchRight() { pitchServo.position = 0.0 }

    fun flipBack() { flipServo.position = 0.0 }
    fun flipForward() { flipServo.position = 0.6 }

    fun grab() {
        gripServo.position = 1.0
        pinched = true
    }

    fun release() {
        gripServo.position = 0.0
        pinched = false
    }

    fun toggleGrip() {
        if(pinched) release()
        else        grab()
    }
}
