package test

import org.firstinspires.ftc.teamcode.geometry.Range
import org.junit.Assert.*
import org.junit.Test

class RangeOverlapsTest {

    @Test
    fun overlappingRanges() {
        val a = Range(1, 5)
        val b = Range(4, 8)

        assertTrue(a overlaps b)
        assertTrue(b overlaps a)
    }

    @Test
    fun touchingAtBoundary() {
        val a = Range(1, 5)
        val b = Range(5, 10)

        assertTrue(a overlaps b)
        assertTrue(b overlaps a)
    }

    @Test
    fun nonOverlappingRanges() {
        val a = Range(1, 3)
        val b = Range(4, 6)

        assertFalse(a overlaps b)
        assertFalse(b overlaps a)
    }

    @Test
    fun oneInsideAnother() {
        val a = Range(1, 10)
        val b = Range(3, 7)

        assertTrue(a overlaps b)
        assertTrue(b overlaps a)
    }
}