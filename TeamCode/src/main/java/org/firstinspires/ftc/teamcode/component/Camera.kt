package org.firstinspires.ftc.teamcode.component

import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline
import org.openftc.easyopencv.OpenCvWebcam
import java.util.concurrent.TimeUnit

class Camera(
    val deviceSupplier: () -> OpenCvWebcam?,
    resolution: Vector2D,
    pipeline: OpenCvPipeline,
    orientation: OpenCvCameraRotation = OpenCvCameraRotation.UPRIGHT
) {
    private var _hwDeviceBacker: OpenCvWebcam? = null
    val camera: OpenCvWebcam get() {
        if(_hwDeviceBacker == null){
            _hwDeviceBacker = deviceSupplier() ?: error(
                "tried to access hardware before OpMode init"
            )
        }
        return _hwDeviceBacker!!
    }

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
            //FtcDashboard.getInstance().startCameraStream(camera, 120.0)
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
}