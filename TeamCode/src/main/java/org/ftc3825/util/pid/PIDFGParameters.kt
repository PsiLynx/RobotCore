package org.ftc3825.util.pid

import java.util.function.DoubleSupplier

class PIDFGParameters(
    val P: () -> Double,
    val I: () -> Double = { 0.0 },
    val D: () -> Double = { 0.0 },
    val F: () -> Double = { 0.0 },
    val G: () -> Double = { 0.0 }
) {
    constructor(
        P: Number = 0, I: Number = 0, D: Number = 0, F: Number = 0, G: Number = 0
    ) : this(
        { P.toDouble() },
        { I.toDouble() },
        { D.toDouble() },
        { F.toDouble() },
        { G.toDouble() }
    )
}