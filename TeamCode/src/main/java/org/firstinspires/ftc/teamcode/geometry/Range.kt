package org.firstinspires.ftc.teamcode.geometry

import kotlin.math.abs
import kotlin.math.sign

class Range(
    start: Number = 0,
    end: Number = 1,
) {
    var start = start.toDouble()
    var end = end.toDouble()

    fun size(): Double{
        return abs(end-start)
    }
    val sign get() = (end-start).sign

    infix fun overlaps(other: Range) = (
           ( this.end >= other.start && other.end >= this.start )
        || ( other.end >= this.start && this.end >= other.start )
    )
}