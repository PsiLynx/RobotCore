package org.firstinspires.ftc.teamcode.util.control

import kotlin.math.cos
import kotlin.math.sign

class PIDFController(
    val P: () -> Double = { 0.0 },
    val I: () -> Double = { 0.0 },
    val D: () -> Double = { 0.0 },
    val absF: () -> Double = { 0.0 },
    val relF: () -> Double = { 0.0 },
    val G: () -> Double = { 0.0 },
    var targetPosition: Double = 0.0,
    val pos: () -> Double,
    val setpointError: PIDFController.() -> Double = {
        targetPosition - pos()
    },
    val apply: (Double) -> Unit,
){
    constructor(P: Double = 0.0,
                I: Double = 0.0,
                D: Double = 0.0,
                absF: Double = 0.0,
                relF: Double = 0.0,
                G: Double = 0.0,
                targetPosition: Double = 0.0,
                pos: () -> Double,
                setpointError: PIDFController.() -> Double = {
                    this.targetPosition - pos()
                },
                apply: (Double) -> Unit,
    ): this(
        { P },
        { I },
        { D },
        { absF },
        { relF },
        { G },
        targetPosition = targetPosition,
        pos = pos,
        setpointError = setpointError,
        apply = apply
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
        error = setpointError.invoke(this)

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