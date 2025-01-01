package test

import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import org.ftc3825.command.FollowPathCommand
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.gvf.HeadingType.Tangent
import org.ftc3825.gvf.Line
import org.ftc3825.gvf.Path
import org.ftc3825.gvf.Spline
import org.ftc3825.sim.maxDriveVelocity
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.util.Pose2D
import org.ftc3825.util.TestClass
import org.ftc3825.util.Vector2D
import org.ftc3825.util.graph.Field
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
            val line = Line(v1, v2, Tangent())

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
                    0, -1,
                    50, -1,
                    Tangent()
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
                    Tangent()
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
                    Tangent()
                ),
                Spline(
                    50, -1,
                    20, 0,
                    70, 50,
                    0, 30,
                    Tangent()
                ),
                Line(
                    70, 50,
                    70, 100,
                    Tangent()
                )
            )
        )

        test(path)
    }

    private fun test(path: Path) {
        val field = Field(
            Vector2D(-40, -40),
            Vector2D(40, 40),
            step = 4
        )

        Drivetrain.position = Pose2D(0, 0, 0)
        Drivetrain.reset()
        Drivetrain.components.forEach { it.reset() }
        Drivetrain.components.forEach { it.hardwareDevice.resetDeviceConfigurationForOpMode() }
        val command = FollowPathCommand(path)

        command.schedule()

        for(i in 0..100*path.numSegments) {
            CommandScheduler.update()
            field.put(
                Drivetrain.position.vector,
                ('A'..'Z').withIndex().minBy {
                    it.index - ( Drivetrain.velocity.mag / maxDriveVelocity ) * 26
                }.value
            )
            if(i % 3 == 0){
                println(Drivetrain.position.vector)
            }
            if(command.isFinished()){
                break
            }
        }
        field.put(0, 0, '*')
        //field.print()

        assertTrue(
            (Drivetrain.position.vector - path[-1].end).mag < 0.5
        )
    }
}
