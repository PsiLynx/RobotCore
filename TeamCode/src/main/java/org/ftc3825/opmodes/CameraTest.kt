import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.cv.GamePiecePipeLine
import org.ftc3825.opmodes.CommandOpMode
import org.ftc3825.opmodes.Teleop
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.OuttakeSlides
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.subsystem.V2Claw
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import kotlin.math.floor


@TeleOp(name = "Red Blob Detection")
class CameraTest : CommandOpMode() {
    override fun init() {

        V2Claw.justUpdate().schedule()

        Telemetry.telemetry = telemetry!!
        Telemetry.addFunction("samples") { V2Claw.getSamples() }
        Telemetry.addFunction("\n") { CommandScheduler.status() }
        Telemetry.justUpdate().schedule()
    }


}
