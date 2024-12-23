package org.ftc3825.component

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.util.Vector2D
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline
import org.openftc.easyopencv.OpenCvWebcam

class Camera(
    name: String,
    resolution: Vector2D,
    pipeline: OpenCvPipeline
): Component {

    override var lastWrite = LastWrite.empty()

    override val hardwareDevice = GlobalHardwareMap.get(
        WebcamName::class.java,
        name
    )
    private val identifier =
        GlobalHardwareMap.getIdentifier(
            "cameraMonitorViewId",
            "id",
            GlobalHardwareMap.appContext.packageName
        )

    val camera: OpenCvWebcam = OpenCvCameraFactory.getInstance().createWebcam(
        hardwareDevice,
        identifier
    )
    init {
        camera.openCameraDeviceAsync(object: AsyncCameraOpenListener {
            override fun onOpened() {
                camera.setPipeline(pipeline)
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


    override fun update(deltaTime: Double) { }
    override fun resetInternals() { }
}