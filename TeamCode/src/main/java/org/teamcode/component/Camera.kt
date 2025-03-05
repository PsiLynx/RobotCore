package org.teamcode.component

import com.acmerobotics.dashboard.FtcDashboard
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.teamcode.command.internal.GlobalHardwareMap
import org.teamcode.util.geometry.Vector2D
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline
import org.openftc.easyopencv.OpenCvWebcam
import java.util.concurrent.TimeUnit

class Camera(
    name: String,
    resolution: Vector2D,
    pipeline: OpenCvPipeline,
    orientation: OpenCvCameraRotation = OpenCvCameraRotation.UPRIGHT
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
    var exposureMs: Double
        get() = (
            camera.exposureControl.getExposure(TimeUnit.NANOSECONDS).toDouble()
            / 1e6
        )
        set(value) {
            camera.exposureControl.setExposure(
                (value * 1e6).toLong(),
                TimeUnit.NANOSECONDS
            )
        }
    init {
        try {
            camera.openCameraDevice()
            camera.setPipeline(pipeline)
            camera.startStreaming(
                resolution.x.toInt(),
                resolution.y.toInt(),
                orientation,
                OpenCvWebcam.StreamFormat.MJPEG
            )
            FtcDashboard.getInstance().startCameraStream(camera, 120.0)
            println(
                "camera exposure supported: ${
                    camera.exposureControl.isExposureSupported
                }"
            )
        }
        catch (e: Exception){
            println("ERROR OPENING CAMERA")
        }
    }


    override fun update(deltaTime: Double) { }
    override fun resetInternals() { }
}