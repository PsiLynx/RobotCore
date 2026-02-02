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
}