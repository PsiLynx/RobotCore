package test

import org.ftc3825.subsystem.Localizer
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.GVF.Line
import org.ftc3825.GVF.Spline
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.util.TestClass
import org.ftc3825.util.Vector2D
import org.ftc3825.util.assertWithin
import org.ftc3825.util.inches
import org.ftc3825.util.Pose2D
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Random


class GVFTest: TestClass() {

    val rand = Random()

    @Test fun closestT() {
        val max = 10000
        val maxD = max.toDouble()
        for (i in 0..1000){
            rand.setSeed(i.toLong())

            val v1 = Vector2D(rand.nextInt(), rand.nextInt())
            val v2 = Vector2D(rand.nextInt(), rand.nextInt())
            val line = Line(v1, v2)

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
        val path = org.ftc3825.GVF.Path(
            Line(
                inches(0), inches(-1),
                inches(50), inches(-1)
            )
        )
        test(path)

    }
    @Test fun splineTest() {
        val path = org.ftc3825.GVF.Path(
            Spline(
                inches(0), inches(0),
                inches(30), inches(0),
                inches(20), inches(50),
                inches(20), inches(30)
            )
        )
        test(path)
    }

    @Test fun sequenceTest() {
        val path = org.ftc3825.GVF.Path(
            Line(
                inches(0), inches(-1),
                inches(50), inches(-1)
            ),
            Spline(
                inches(50), inches(-1),
                30, 0,
                inches(70), inches(50),
                0, -20
            ),
            Line(
                inches(70), inches(50),
                inches(70), inches(100)
            )
        )

        test(path)
    }

    private fun test(path: org.ftc3825.GVF.Path) {
        Drivetrain.init(hardwareMap)
        Localizer.init(hardwareMap)

        Localizer.position = Pose2D(0, 0, 0)
        Localizer.encoders.forEach { it.reset() }
        FollowPathCommand(path).schedule()

        for(i in 0..1000*path.numSegments) {
            CommandScheduler.update()
            if(i % 100 == 0){
                println(Localizer.position.vector)
            }
        }

        assertTrue(
            (Localizer.position.vector - path[-1].end).mag < 0.5
        )
    }

}
