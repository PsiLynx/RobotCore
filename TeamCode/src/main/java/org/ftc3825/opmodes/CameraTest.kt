import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.ftc3825.cv.GamePiecePipeLine
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation


@TeleOp(name = "Red Blob Detection")
class CameraTest : LinearOpMode() {

    override fun runOpMode() {
        var cameraMonitorViewId = hardwareMap.appContext.resources.getIdentifier(
            "cameraMonitorViewId",
            "id",
            hardwareMap.appContext.packageName
        )

        var webcamName = hardwareMap.get(WebcamName::class.java, "Webcam 1")

        val camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId)

        camera.openCameraDeviceAsync(object : AsyncCameraOpenListener {
            override fun onOpened() {
                camera.setPipeline(GamePiecePipeLine())
                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT)
            }

            override fun onError(errorCode: Int) {
                println(" **** camera open error **** ")
            }
        })

        waitForStart()
        while(opModeIsActive()) { }
    }



}
