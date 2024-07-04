package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.GVF.Line
import org.firstinspires.ftc.teamcode.util.Vector2D
import org.junit.Assert.assertTrue
import org.junit.Test

import kotlin.math.abs
import java.util.Random
import kotlin.random.asJavaRandom

private const val max = 10000
private val maxD = max.toDouble()
class LineTest {

    val rand = Random()

    @Test
    fun closestT() {
        for (i in 0..1000){
            rand.setSeed(i.toLong())

            var v1 = Vector2D(rand.nextInt(), rand.nextInt())
            var v2 = Vector2D(rand.nextInt(), rand.nextInt())
            var line = Line(v1, v2)

            val expected = (0..max).minBy { (v2 * (it / maxD) + v1 * (1 - (it / maxD))).mag } / maxD
            //println("expected: $expected")
            //println("actual:   " + line.closestT((Vector2D(0,0))) + "\n")
            assertTrue(
                abs(expected - line.closestT(Vector2D(0, 0))) < 1e-4
            )
        }
    }
}