package test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.command.RamseteCommand
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardware
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakePinpoint
import org.firstinspires.ftc.teamcode.gvf.Circle
import org.firstinspires.ftc.teamcode.gvf.Line
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.gvf.Spline
import org.firstinspires.ftc.teamcode.gvf.path
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.OpModeRunner
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.tangent
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.millis
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.ftc.HardwareMapWrapper
import org.psilynx.psikit.ftc.OpModeControls
import org.psilynx.psikit.ftc.wrappers.MotorWrapper
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class RamseteTest: TestClass() {
    var done = false

    @Test fun lineTest() =
        test(
            path {
                start(0, -2)
                lineTo(50, -2, tangent)
            }
        )

    @Test fun splineTest() =
        test(
            path {
                start(0, 0)
                curveTo(
                    40, 0,
                    0, 40,
                    50, 50,
                    tangent
                )
            }
        )

    @Test fun sequenceTest() =
        test(
            path {
                start(0, -1)
                lineTo(50, -1, tangent)
                endVel(0.2)
                curveTo(
                    40, 0,
                    -40, 0,
                    50, 50,
                    tangent
                )
                lineTo(0, 50, tangent)
            }
        )
    @Test fun arcTest() = test(
        path {
            start(-1, -1)
            lineTo(20, -1, tangent)
            arcLeft(
                degrees(90),
                r = 15,
                tangent
            )
            straight(20, tangent)
        }
    )
    @Test fun nanTest() {
        splineTest()
    }

    private fun test(path: Path) {
        CommandScheduler.reset()
        Logger.reset()
        FakeTimer.time = 0.0

        if(USE_OP_MODE) {
            OpModeRunner(
                @Autonomous object : CommandOpMode() {
                    override fun postSelector() {
                        this.hardwareMap = FakeHardwareMap
                        setupTest(path, this)
                    }
                }
            ).run()
        }
        else {
            setupTest(path)
            Logger.start()
            while (FakeTimer.time < 4 * path.numSegments + 5){
                Logger.periodicBeforeUser()
                TankDrivetrain.motors.forEach {
                    Logger.processInputs(
                        "motor " + (it.hardwareDevice as DcMotorEx).portNumber,
                        (it.hardwareDevice) as MotorWrapper
                    )
                }
                FakeTimer.addTime(millis(3))
                CommandScheduler.update()
                Logger.periodicAfterUser(0.0, 0.0)
            }
        }

    }
    fun setupTest(path: Path, opMode: OpMode? = null) {

        done = false
        println("testing ramsete")

        TankDrivetrain.reset()
        TankDrivetrain.position = Pose2D(0.0, 0.0, 0.0)
        FakeTimer.time = 0.0

        val command = RamseteCommand(path)

        command.initialize()
        command.schedule()
        RunCommand {

            if(FakeTimer.time > 4 * path.numSegments){
                OpModeControls.stopped = true
                if(opMode != null){ endOpMode(opMode) }
                if(CommandScheduler.commands.contains(command)){
                    assert(false)
                }
            }
            if(!CommandScheduler.commands.contains(command)){
                OpModeControls.stopped = true
                if(opMode != null){ endOpMode(opMode) }
                done = true
            }
            //println(TankDrivetrain.position.vector)

        }.schedule()
    }
}
