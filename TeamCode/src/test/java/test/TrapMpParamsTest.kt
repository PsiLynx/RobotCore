package test

import org.firstinspires.ftc.teamcode.controller.params.TrapMpParams
import org.junit.Test
import org.junit.Assert.*

class TrapMpParamsTest {


    @Test
    fun testVelAtT_FullPlateau() {
        val mp = TrapMpParams(
            dist = 16.0,
            v_max = 4.0,
            v_0 = 0.0,
            v_f = 0.0,
            a_max = 2.0
        )
        // 2s accel, 2s coast, 2s decel
        assertEquals(0.0, mp.vel(0.0), 1e-6)
        assertEquals(2.0, mp.vel(1.0), 1e-6)
        assertEquals(4.0, mp.vel(2.0), 1e-6)
        assertEquals(4.0, mp.vel(3.0), 1e-6)
        assertEquals(2.0, mp.vel(5.0), 1e-6)
        assertEquals(0.0, mp.vel(6.0), 1e-6)
    }

    @Test
    fun testVelAtT_NoPlateau_ShortDistance() {
        val mp = TrapMpParams(
            dist = 2.0,
            v_max = 10.0,
            v_0 = 0.0,
            v_f = 0.0,
            a_max = 2.0
        )
        // Accelerate then immediately decelerate
        assertEquals(0.0, mp.vel(0.0), 1e-6)
        val peak = mp.vel(0.7)
        assertTrue(peak < mp.v_max)
        assertEquals(1.2, mp.vel(1.4), 1e-6)
    }

    @Test
    fun testVelAtT_NonZeroStartAndEnd() {
        val mp = TrapMpParams(
            dist = 8.0,
            v_max = 5.0,
            v_0 = 2.0,
            v_f = 2.0,
            a_max = 3.0
        )
        for(i in 0..100){
            println("${i / 100.0 * 2.2}, " + mp.vel(i / 100.0 * 2.2))
        }
        // Starts at 2, accelerates to 5, decelerates back to 2
        assertEquals(2.0, mp.vel(0.0), 1e-6)
        assertEquals(3.5, mp.vel(0.5), 1e-6)
        assertEquals(5.0, mp.vel(1.1), 1e-6)
        assertEquals(3.5, mp.vel(1.7), 1e-6)
        assertEquals(2.0, mp.vel(2.2), 1e-6)
    }
}