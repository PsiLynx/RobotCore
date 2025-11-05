package org.firstinspires.ftc.teamcode.component

import android.util.Size
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline
import org.openftc.easyopencv.OpenCvWebcam
import java.util.concurrent.TimeUnit

class Camera(
    val deviceSupplier: () -> CameraName?,
    val resolution: Vector2D,
) {
    private var _hwDeviceBacker: CameraName? = null
    val camera: CameraName get() {
        if(_hwDeviceBacker == null){
            _hwDeviceBacker = deviceSupplier() ?: error(
                "tried to access hardware before OpMode init"
            )
        }
        return _hwDeviceBacker!!
    }
    lateinit var visionPortal: VisionPortal

    val aprilTagProcessor = AprilTagProcessor.Builder().build()

    val detections get() = aprilTagProcessor.detections

    fun build() {
        visionPortal =
            VisionPortal.Builder()
                .setCamera(camera)
                .setCameraResolution(
                    Size(resolution.x.toInt(), resolution.y.toInt())
                )
                .addProcessor(aprilTagProcessor)
                .build()
    }

}