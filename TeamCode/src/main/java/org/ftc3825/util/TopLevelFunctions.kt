package org.ftc3825.util

import kotlin.math.abs

/**
 * syntactical sugar to go with of, as in x isWithin 0.1 of 0
 */
infix fun Number.isWithin(other: Number) = this.toDouble() to other.toDouble()
/**
 * syntactical sugar to go with Number.isWithin, first is within second of other
 */
infix fun Pair<Double, Double>.of(other: Number) =
    abs(this.first - other.toDouble()) <= this.second
