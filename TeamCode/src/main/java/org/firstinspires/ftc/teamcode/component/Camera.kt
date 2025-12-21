package org.firstinspires.ftc.teamcode.component

import android.util.Size
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Position
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline
import org.openftc.easyopencv.OpenCvWebcam
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class Camera(
    val deviceSupplier: () -> CameraName?,
    val resolution: Vector2D,
    val cameraPose: Vector3D,
    val cameraOrientation: YawPitchRollAngles
) {
    private var _hwDeviceBacker: CameraName? = null
    var built = false
    val camera: CameraName get() {
        if(_hwDeviceBacker == null){
            _hwDeviceBacker = deviceSupplier() ?: error(
                "tried to access hardware before OpMode init"
            )
        }
        return _hwDeviceBacker!!
    }
    lateinit var visionPortal: VisionPortal

    lateinit var aprilTagProcessor: AprilTagProcessor

    val detections: ArrayList<AprilTagDetection?>?
        get() = aprilTagProcessor.detections

    val robotPose = Pose2D()
    fun enable() {
        visionPortal.setProcessorEnabled(aprilTagProcessor, true)
    }

    fun build() {
        if(!built) {
            aprilTagProcessor = (
                    AprilTagProcessor.Builder()
                        .setCameraPose(
                            Position(
                                DistanceUnit.INCH,
                                cameraPose.x,
                                cameraPose.z,
                                -cameraPose.y,
                                0L
                            ), cameraOrientation
                        )
                        .build()
                    )
            visionPortal = (
                    VisionPortal.Builder()
                        .setCamera(camera)
                        .setCameraResolution(
                            Size(resolution.x.toInt(), resolution.y.toInt())
                        )
                        .addProcessor(aprilTagProcessor)
                        .build()
                    )
            visionPortal.setProcessorEnabled(aprilTagProcessor, true)
        }
        built = true
    }
}