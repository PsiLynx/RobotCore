package test

import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.fakehardware.FakePinpoint
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.constant
import org.firstinspires.ftc.teamcode.gvf.Line
import org.firstinspires.ftc.teamcode.gvf.Path
import org.firstinspires.ftc.teamcode.gvf.Spline
import org.firstinspires.ftc.teamcode.gvf.path
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.geometry.Vector2D
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Random
import kotlin.math.PI


class GVFTest: TestClass() {

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

    @Test fun lineTest() =
        test(
            path {
                start(0, -2)
                lineTo(50, -2, constant(PI / 2))
            }
        )

    @Test fun splineTest() =
        test(
            path {
                start(0, 0)
                curveTo(
                    70, 0,
                    0, 70,
                    50, 50,
                    constant(PI / 2)
                )
            }
        )

    @Test fun sequenceTest() =
        test(
            path {
                start(0, -1)
                lineTo(50, -1, constant(PI / 2))
                curveTo(
                    20, 0,
                    0, 30,
                    70, 50,
                    constant(PI / 2)
                )
                lineTo(70, 100, constant(PI / 2))
            }
        )
    @Test fun nanTest() {
        (Drivetrain.pinpoint.hardwareDevice as FakePinpoint).chanceOfNaN = 0.2
        splineTest()
        (Drivetrain.pinpoint.hardwareDevice as FakePinpoint).chanceOfNaN = 0.0
    }

    private fun test(path: Path) {
        CommandScheduler.reset()
        Drivetrain.reset()
        Drivetrain.position = Pose2D(0.01, 0.01, PI / 2)
        val command = FollowPathCommand(path)

        command.schedule()

        var passing = false
        for(i in 0..500*path.numSegments) {
            CommandScheduler.update()
            println( (Drivetrain.pinpoint.hardwareDevice as FakePinpoint)._pos.vector )

            if(command.isFinished()){passing = true; break }
        }

        assertTrue(passing)
    }
}
