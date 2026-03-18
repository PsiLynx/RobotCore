package test

import org.firstinspires.ftc.teamcode.controller.mp.LerpedConstrainedMP
import org.firstinspires.ftc.teamcode.controller.mp.TrapMpParams
import org.junit.Assert.*
import org.junit.Test

class LerpedConstrainedMPTest {

    @Test
    fun testInitialVelocityAtZero() {
        val v0 = 0.5
        val vft = 1.0
        val amax = 2.0
        val dmax = 2.0
        val dist = 1.0
        val ppi = 10

        val alwaysLarge = listOf<(Double) -> Double>({ _ -> Double.POSITIVE_INFINITY })
        val mp = LerpedConstrainedMP(
            v_0 = v0,
            v_f_target = vft,
            a_max = amax,
            d_max = dmax,
            dist = dist,
            velocityMaxes = alwaysLarge,
            ppi = ppi
        )

        // v at x = 0.0 should be (approximately) the first table entry, which is v0
        val vAtZero = mp.v(0.0)
        assertEquals("v(0.0) should equal v_0", v0, vAtZero, 1e-9)
    }

    @Test
    fun testBackwardClampToVfTarget() {
        // choose parameters so forward pass would produce a final > v_f_target,
        // then constructor should clamp the last table entry to v_f_target.
        val v0 = 5.0
        val vft = 1.0
        val amax = 5.0
        val dmax = 5.0
        val dist = 2.0
        val ppi = 1

        val alwaysLarge = listOf<(Double) -> Double>({ _ -> Double.POSITIVE_INFINITY })
        val mp = LerpedConstrainedMP(
            v_0 = v0,
            v_f_target = vft,
            a_max = amax,
            d_max = dmax,
            dist = dist,
            velocityMaxes = alwaysLarge,
            ppi = ppi
        )

        // the last table entry should have been clamped to v_f_target by the backward pass
        assertEquals("final table value (v_f) should equal v_f_target", vft, mp.v_f, 1e-9)
        // also explicitly check the last entry in the table array
        assertEquals("table.last() should equal v_f_target", vft, mp.table.last(), 1e-9)
    }

    @Test
    fun testVelocityMaxesCapTable() {
        // Use a velocityMaxes function that caps velocity to 1.0 everywhere.
        val v0 = 0.0
        val vft = 2.0
        val amax = 100.0 // large so forward sqrt would otherwise be larger than 1.0
        val dmax = 100.0
        val dist = 1.0
        val ppi = 10

        val capOne = listOf<(Double) -> Double>({ _ -> 1.0 })
        val mp = LerpedConstrainedMP(
            v_0 = v0,
            v_f_target = vft,
            a_max = amax,
            d_max = dmax,
            dist = dist,
            velocityMaxes = capOne,
            ppi = ppi
        )

        // every table entry should be <= 1.0 (within tolerance)
        mp.table.forEachIndexed { idx, v ->
            assertTrue("table[$idx] should be <= 1.0", v <= 1.0 + 1e-9)
        }

        // a sample interpolation: x between 0 and 0.1 (with ppi=10)
        val x = 0.05
        val expected = mp.table[0] + (mp.table[1] - mp.table[0]) * 0.5
        assertEquals("v(x) should linearly interpolate between neighboring table entries",
            expected, mp.v(x), 1e-9)
    }

}
