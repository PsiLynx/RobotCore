package org.firstinspires.ftc.teamcode.util.control

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sqrt

open class SquIDController(
    P: () -> Double = { 0.0 },
    I: () -> Double = { 0.0 },
    D: () -> Double = { 0.0 },
    absF: () -> Double = { 0.0 },
    relF: () -> Double = { 0.0 },
    G: () -> Double = { 0.0 },
    setpointError: () -> Double = { 0.0 },
    apply: (Double) -> Unit = { },
    pos: () -> Double = { 0.0 }
): PIDFController(P, I, D, absF, relF, G, setpointError, apply, pos){

    constructor(P: Double = 0.0,
                I: Double = 0.0,
                D: Double = 0.0,
                absF: Double = 0.0,
                relF: Double = 0.0,
                G: Double = 0.0,
                setpointError: () -> Double = { 0.0 },
                apply: (Double) -> Unit = { },
                pos: () -> Double = { 0.0 }
    ): this({ P }, { I }, { D }, { absF }, { relF }, { G }, setpointError, apply, pos)
    constructor(params: PIDFGParameters,
                setpointError: () -> Double = { 0.0 },
                apply: (Double) -> Unit = { },
                pos: () -> Double = { 0.0 }
    ): this(
        params.P,
        params.I,
        params.D,
        params.absF,
        params.relF,
        params.G,
        setpointError,
        apply,
        pos
    )
    override val feedback: Double
        get() {
            val effort = (
                  P() * sqrt( abs(error) ) * error.sign
                + I() * accumulatedError
                + D() * (error - lastError)
                + G() * cos(pos())
                + absF()
            )
            return ( effort + relF() * effort.sign ).coerceIn(-1.0, 1.0)
        }
}