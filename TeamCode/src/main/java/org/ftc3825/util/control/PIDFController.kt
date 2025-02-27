package org.ftc3825.util.control

import kotlin.math.cos
import kotlin.math.sign

open class PIDFController(
    open var P: () -> Double = { 0.0 },
    open var I: () -> Double = { 0.0 },
    open var D: () -> Double = { 0.0 },
    open var absF: () -> Double = { 0.0 },
    open var relF: () -> Double = { 0.0 },
    open var G: () -> Double = { 0.0 },
    open var setpointError: () -> Double = { 0.0 },
    open var apply: (Double) -> Unit = { },
    open var pos: () -> Double = { 0.0 }
){
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


    var lastError = 0.0
    var error = 0.0

    var accumulatedError = 0.0

    /**
     * error as  ( reference point - current)
     * for angle error, use radians
     */

    fun updateController(deltaTime: Double) {
        updateError(deltaTime)

        apply(feedback)
    }

    fun updateError(deltaTime: Double) {
        lastError = error
        error = setpointError()

        accumulatedError += error * deltaTime
        if(accumulatedError.isNaN()) accumulatedError = 0.0
    }

    fun resetController() {
        lastError = setpointError()
        error = setpointError()
        accumulatedError = 0.0
    }

    val feedback: Double
        get() {
            val effort = (
                P() * error
                + I() * accumulatedError
                + D() * (error - lastError)
                + G() * cos(pos())
                + absF()
            )
            return (
                effort + relF() * error.sign
            ).coerceIn(-1.0, 1.0)
        }
}