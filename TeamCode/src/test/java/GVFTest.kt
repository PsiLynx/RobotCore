package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.GVF.Line
import org.firstinspires.ftc.teamcode.GVF.Path
import org.firstinspires.ftc.teamcode.GVF.Spline
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.command.FollowPathCommand
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeLocalizer
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.Vector2D
import org.firstinspires.ftc.teamcode.util.inches
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Random
import kotlin.math.abs


class GVFTest: TestClass() {
    var localizer = FakeLocalizer(hardwareMap)

    val rand = Random()

    @Test
    fun closestT() {
        val max = 10000
        val maxD = max.toDouble()
        for (i in 0..1000){
            rand.setSeed(i.toLong())

            val v1 = Vector2D(rand.nextInt(), rand.nextInt())
            val v2 = Vector2D(rand.nextInt(), rand.nextInt())
            val line = Line(v1, v2)

            val expected = (0..max).minBy { (v2 * (it / maxD) + v1 * (1 - (it / maxD))).mag } / maxD
            assertTrue(
                abs(expected - line.closestT(Vector2D(0, 0))) < 1e-4
            )
        }
    }

    @Test
    fun lineTest() {
        val path = Path(
            Line(
            inches(0), inches(-1),
            inches(50), inches(-1)
            )
        )
        test(path)

    }
    @Test
    fun splineTest() {
        val path = Path(
            Spline(
                inches(0), inches(0),
                inches(30), inches(0),
                inches(20), inches(50),
                inches(20), inches(30)
            )
        )
        test(path)
    }

    @Test
    fun sequenceTest() {
        val path = Path(
            Line(
                inches(0), inches(-1),
                inches(50), inches(-1)
            ),
            Spline(
                inches(50), inches(-1),
                inches(80), inches(-1),
                inches(70), inches(50),
                inches(70), inches(30)
            ),
            Line(
                inches(70), inches(50),
                inches(70), inches(100)
            )
        )

        test(path)
    }

    private fun test(path: Path) {
        localizer = FakeLocalizer(hardwareMap)
        CommandScheduler.schedule(FollowPathCommand(localizer, path))
        for(i in 0..1000*path.length) {
            CommandScheduler.update()
        }
        assertTrue( (localizer.position.vector - path[-1].end).mag < inches(0.5))
    }

}