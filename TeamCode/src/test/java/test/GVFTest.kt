package test

import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.GVF.Line
import org.ftc3825.GVF.Spline
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.util.TestClass
import org.ftc3825.util.Vector2D
import org.ftc3825.util.assertWithin
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
                0, -1,
                50, -1
            )
        )
        test(path)

    }
    @Test fun splineTest() {
        val path = org.ftc3825.GVF.Path(
            Spline(
                0, 0,
                30, 0,
                20, 50,
                20, 30
            )
        )
        test(path)
    }

    @Test fun sequenceTest() {
        val path = org.ftc3825.GVF.Path(
            Line(
                0, -1,
                50, -1
            ),
            Spline(
                50, -1,
                20, 0,
                70, 50,
                0, 30
            ),
            Line(
                70, 50,
                70, 100
            )
        )

        test(path)
    }

    private fun test(path: org.ftc3825.GVF.Path) {

        Drivetrain.position = Pose2D(0, 0, 0)
        Drivetrain.delta = Pose2D(0, 0, 0)
        Drivetrain.reset()
        //Drivetrain.encoders.forEach { it.resetPosition() }
        Drivetrain.components.forEach { it.reset() }
        Drivetrain.components.forEach { it.hardwareDevice.resetDeviceConfigurationForOpMode() }
        val command = FollowPathCommand(path)

        command.schedule()

        for(i in 0..1000*path.numSegments) {
            CommandScheduler.update()
            if(i % 30 == 0){
                println(Drivetrain.position.vector)
            }
            if(command.isFinished()){
                break
            }
        }

        assertTrue(
            (Drivetrain.position.vector - path[-1].end).mag < 0.5
        )
    }

}
