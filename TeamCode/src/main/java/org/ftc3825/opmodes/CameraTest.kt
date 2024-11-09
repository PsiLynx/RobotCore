import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline

@TeleOp(name = "Red Blob Detection")
class RedBlobDetectionOpMode : LinearOpMode() {

    private lateinit var camera: OpenCvCamera
    private val pipeline = RedBlobPipeline() // Your custom pipeline class

    override fun runOpMode() {
        // Initialize the camera
        val cameraMonitorViewId = hardwareMap.appContext.resources.getIdentifier(
            "cameraMonitorViewId", "id", hardwareMap.appContext.packageName
        )
        camera = OpenCvCameraFactory.getInstance().createInternalCamera(
            OpenCvCameraFactory.CameraDirection.BACK, cameraMonitorViewId
        )

        // Set the pipeline and start streaming
        camera.setPipeline(pipeline)
        camera.openCameraDeviceAsync(object : OpenCvCamera.AsyncCameraOpenListener {
            override fun onOpened() {
                // Start streaming at a desired resolution and orientation
                camera.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT)
            }

            override fun onError(errorCode: Int) {
                telemetry.addData("Camera Error", errorCode)
                telemetry.update()
            }
        })

        // Wait for the OpMode to start
        waitForStart()

        // Run the OpMode until it's stopped
        while (opModeIsActive()) {
            telemetry.addData("Frame Count", camera.frameCount)
            telemetry.addData("FPS", String.format("%.2f", camera.fps))
            telemetry.addData("Total frame time ms", camera.totalFrameTimeMs)
            telemetry.addData("Pipeline time ms", camera.pipelineTimeMs)
            telemetry.update()
        }

        // Stop the camera when the OpMode ends
        camera.stopStreaming()
    }
}
