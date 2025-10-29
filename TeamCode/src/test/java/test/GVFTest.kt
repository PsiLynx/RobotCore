package test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.fakehardware.FakePinpoint
import org.firstinspires.ftc.teamcode.gvf.Circle
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.constant
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.forward
import org.firstinspires.ftc.teamcode.gvf.Line
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.gvf.Spline
import org.firstinspires.ftc.teamcode.gvf.path
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.OpModeRunner
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.ftc.OpModeControls
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs


@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class GVFTest: TestClass() {
    @Test fun testCircleCurvature() {
        val circle = Circle(
            Vector2D(10, 12.2),
            2.0,
            forward
        )
        println(circle.r)
        repeat(10) {
            assertWithin(circle.curvature(it / 10.0) - 0.5, 1e-9)
        }
    }
    @Test fun testSplineCurvature() {
        val spline = Spline(
            0, 0,
            10, 0,
            20, 20,
            0, 10,
            forward
        )
        val headings = (0..99).map {
            (
                  spline.point((it + 1) / 100.0)
                - spline.point( it      / 100.0)
            ).theta
        }
        val curvatures = (0..98).map {
            ( headings[it + 1] - headings[it] ) / (
                spline.point((it + 1) / 100.0) - spline.point(it / 100.0)
            ).mag
        }
        println(curvatures)
        var correct = 0
        curvatures.withIndex().forEach {
            println(it.value)
            println(spline.curvature(it.index / 100.0 + 1/200))
            println(it.value / spline.curvature(it.index / 100.0 + 1/200))
            println()
            if(
                abs(
                    spline.curvature(it.index / 100.0 + 1/200) - it.value
                        .toDouble()
                ) < 1e-2
            ) correct ++
        }

        assertGreater(correct, 95)
        // some weird wrapping cases on the numerical approx
    }

    val rand = Random()

    @Test fun closestT() {
        val max = 10000
        val maxD = max.toDouble()
        for (i in 0..1000){
            rand.setSeed(i.toLong())

            val v1 = Vector2D(rand.nextInt(), rand.nextInt())
            val v2 = Vector2D(rand.nextInt(), rand.nextInt())
            val line = Line(v1, v2, constant( PI / 2 ))

            val expected = (0..max).minBy {
                (
                     v2 *       (it / maxD) 
                   + v1 * ( 1 - (it / maxD) )
                ).mag 
            } / maxD

            assertWithin(
                expected - line.closestT( Vector2D(0, 0) ),
                epsilon = 1e-4
            )
        }
    }

    @Test fun circleTest(){
        test(
            Path(arrayListOf(
                Circle(
                    Vector2D(0, 30),
                    30.0,
                    forward
                ).apply { endVelocity = 1.0 }
            ))
        )
    }

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
                start(0, 0)
                curveTo(
                    40, 0,
                    0, 40,
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
                    40, 0,
                    -40, 0,
                    50, 50,
                    forward
                )
                lineTo(0, 50, forward)
            }
        )
    @Test fun nanTest() {
        (Drivetrain.pinpoint.hardwareDevice as FakePinpoint).chanceOfNaN = 0.2
        splineTest()
        (Drivetrain.pinpoint.hardwareDevice as FakePinpoint).chanceOfNaN = 0.0
    }

    private fun test(path: Path) {
        CommandScheduler.reset()
        Logger.reset()
        FakeTimer.time = 0.0

        var passing = false
        println("creating OpMode runner")
        OpModeRunner(
            @Autonomous object : CommandOpMode() {

                override fun initialize() {
                    println("testing gvf")
                    //Drivetrain.reset()
                    Drivetrain.position = Pose2D(0.01, 0.01, PI / 2)
                    val command = FollowPathCommand(path)

                    command.schedule()
                    RunCommand {

                        if(FakeTimer.time > 4 * path.numSegments){
                            OpModeControls.stopped = true
                        }

                        println(Drivetrain.position.vector)
                        if(command.isFinished()){ passing = true }
                        if(passing) OpModeControls.stopped = true
                        sleep(2)

                    }.schedule()
                }
            }
        )//.run()

        //assertTrue(passing)
    }
}
