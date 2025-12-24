package test

import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.fakehardware.FakePinpoint
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.gvf.path
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.ftc.HardwareMapWrapper
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Random
import kotlin.math.PI


@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class CoarseGVFTest: TestClass() {

    val rand = Random()

    @Test fun lineTest() =
        test(
            path {
                start(0, -2)
                lineTo(50, -2, forward)
            }
        )

    @Test fun splineTest() =
        test(
            path {
                start(0, -1)
                curveTo(
                    70, 0,
                    0, 70,
                    50, 50,
                    forward
                )
            }
        )

    @Test fun sequenceTest() =
        test(
            path {
                start(0, -1)
                lineTo(50, -1, forward)
                curveTo(
                    20, 0,
                    0, 30,
                    70, 50,
                    forward
                )
                lineTo(70, 100, forward)
            }
        )
    @Test fun nanTest() {
        splineTest()
    }

    private fun test(path: Path) {
        println("testing gvf")
        if(HardwareMap.hardwareMap !is HardwareMapWrapper){
            HardwareMap.hardwareMap = HardwareMapWrapper(
                HardwareMap.hardwareMap!!
            )
        }
        CommandScheduler.reset()
        TankDrivetrain.reset()
        TankDrivetrain.position = Pose2D(0.01, 0.01, PI / 2)
        val command = FollowPathCommand(path)

        command.schedule()

        var passing = false
        for(i in 0..900*path.numSegments) {
            CommandScheduler.update()
            println(TankDrivetrain.position.vector)

            if(command.isFinished()){passing = true; break }
        }
        if( (TankDrivetrain.position.vector - path[-1].end ).mag < 25 )
            passing = true

        assertTrue(passing)
    }
}
