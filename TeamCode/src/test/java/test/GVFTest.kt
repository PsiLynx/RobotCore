package test

import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.fakehardware.FakeMotor
import org.ftc3825.gvf.HeadingType.Constant
import org.ftc3825.gvf.Line
import org.ftc3825.gvf.Path
import org.ftc3825.gvf.Spline
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.geometry.Pose2D
import org.ftc3825.util.TestClass
import org.ftc3825.util.geometry.Vector2D
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
            val line = Line(v1, v2, Constant( PI / 2 ))

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
                    Constant( PI / 2 )
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
                    30, 0,
                    20, 50,
                    20, 30,
                    Constant( PI / 2 )
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
                    Constant( PI / 2 )
                ),
                Spline(
                    50, -1,
                    20, 0,
                    70, 50,
                    0, 30,
                    Constant( PI / 2 )
                ),
                Line(
                    70, 50,
                    70, 100,
                    Constant( PI / 2 )
                )
            )
        )

        test(path)
    }

    private fun test(path: Path) {
        CommandScheduler.reset()
        Drivetrain.reset()
        Drivetrain.position = Pose2D(0, 0, PI / 2)
        Drivetrain.components.forEach { it.reset() }
        Drivetrain.components.forEach { it.hardwareDevice.resetDeviceConfigurationForOpMode() }
        val command = FollowPathCommand(path)

        command.schedule()

        for(i in 0..100*path.numSegments) {
            CommandScheduler.update()
            if(i % 3 == 0){
                //println(Drivetrain.position.vector)
                println(path.pose(Drivetrain.position, Drivetrain.velocity))
                println(Drivetrain.position.heading)
                println(Drivetrain.motors.map { it.hardwareDevice.power })
                println()

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
