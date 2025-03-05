package org.teamcode.util.control

class PIDFGParameters(
    val P: () -> Double,
    val I: () -> Double = { 0.0 },
    val D: () -> Double = { 0.0 },
    val absF: () -> Double = { 0.0 },
    val relF: () -> Double = { 0.0 },
    val G: () -> Double = { 0.0 },
) {
    constructor(
        P: Number = 0,
        I: Number = 0,
        D: Number = 0,
        absF: Number = 0,
        relF: Number = 0,
        G: Number = 0,
    ) : this(
        { P.toDouble() },
        { I.toDouble() },
        { D.toDouble() },
        { absF.toDouble() },
        { relF.toDouble() },
        { G.toDouble() },
    )
}