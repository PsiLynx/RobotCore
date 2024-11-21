package org.ftc3825.subsystem

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.ftc3825.component.Servo
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.component.Component
import org.ftc3825.cv.GamePiecePipeLine
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Vector2D
import org.ftc3825.util.degrees
import org.ftc3825.util.pitchServoName
import org.ftc3825.util.rollServoName
import org.ftc3825.util.gripServoName
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation

object V2Claw : Subsystem<Claw>() {

    var pinched = false
    private val pitchServo = Servo(pitchServoName)
    private val rollServo = Servo(rollServoName)
    private val gripServo = Servo(gripServoName)

    override val components = arrayListOf<Component>(pitchServo, rollServo, gripServo)

    private val camera = OpenCvCameraFactory.getInstance().createWebcam(
        GlobalHardwareMap.get(
            WebcamName::class.java,
            "Webcam 1"
        ),
        GlobalHardwareMap.hardwareMap.appContext.resources.getIdentifier(
            "cameraMonitorViewId",
            "id",
            GlobalHardwareMap.hardwareMap.appContext.packageName
        )
    )
    val pipeLine = GamePiecePipeLine()
    var resolution = Vector2D(640, 480)

    init {
        camera.openCameraDeviceAsync(object : AsyncCameraOpenListener {
            override fun onOpened() {
                camera.setPipeline(pipeLine)
                camera.startStreaming(
                    resolution.x.toInt(),
                    resolution.y.toInt(),
                    OpenCvCameraRotation.UPRIGHT
                )
            }

            override fun onError(errorCode: Int) {
                println(" **** camera open error **** ")
            }
        })
    }

    fun getSamples() = pipeLine.samples.map {
        Pose2D(it.center.x, it.center.y, degrees(it.angle) ) - ( resolution / 2 ) // center it
    }

    override fun update(deltaTime: Double) { }

    fun pitchUp() { pitchServo.position = 1.0 }
    fun pitchDown() { pitchServo.position = 0.0 }

    fun rollLeft() { rollServo.position = 0.2 }
    fun rollCenter() { rollServo.position = 0.48 }
    fun rollRight() { rollServo.position = 0.8 }

    fun grab() {
        gripServo.position = 0.7
        pinched = true
    }

    fun release() {
        gripServo.position = 1.0
        pinched = false
    }

    fun toggleGrip() {
        if(pinched) release()
        else        grab()
    }
}
