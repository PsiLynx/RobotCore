package test

import org.teamcode.command.FollowPathCommand
import org.teamcode.command.internal.CommandScheduler
import org.teamcode.fakehardware.FakePinpoint
import org.teamcode.gvf.HeadingType.Companion.constant
import org.teamcode.gvf.Line
import org.teamcode.gvf.Path
import org.teamcode.gvf.Spline
import org.teamcode.subsystem.Drivetrain
import org.teamcode.util.geometry.Pose2D
import org.teamcode.sim.TestClass
import org.teamcode.util.geometry.Vector2D
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

    @Test fun lineTest() {
        val path = Path(
            arrayListOf(
                Line(
                    0, 0,
                    50, 0,
                    constant( PI / 2 )
                )
            )
        )
        test(path)

    }
    @Test fun splineTest() {
        val path = Path(
            arrayListOf(
                Spline(
                    0, 0,
                    70, 0,
                    0, 50,
                    -70, 0,
                    constant( PI / 2 )
                )
            )
        )
        test(path)
    }

    @Test fun sequenceTest() {
        val path = Path(
            arrayListOf(
                Line(
                    0, -1,
                    50, -1,
                    constant( PI / 2 )
                ),
                Spline(
                    50, -1,
                    20, 0,
                    70, 50,
                    0, 30,
                    constant( PI / 2 )
                ),
                Line(
                    70, 50,
                    70, 100,
                    constant( PI / 2 )
                )
            )
        )

        test(path)
    }
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
