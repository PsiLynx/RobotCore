package org.ftc3825.util.pid

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sign

interface PIDFController {

    var p: () -> Double
    var i: () -> Double
    var d: () -> Double
    var absF: () -> Double
    var relF: () -> Double
    var g: () -> Double

    var lastError: Double
    var error: Double

    var accumulatedError: Double

    fun initializeController(parameters: PIDFGParameters) {
        p = parameters.P
        i = parameters.I
        d = parameters.D
        absF = parameters.absF
        relF = parameters.relF
        g = parameters.G

        lastError = setpointError()
        error = setpointError()
    }

    /**
     * error as  ( reference point - current)
     * for angle error, use radians
     */
    var setpointError: () -> Double
    var pos: () -> Double
    fun applyFeedback(feedback: Double)

    fun updateController(deltaTime: Double) {
        updateError(deltaTime)

        applyFeedback(feedback)
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
                p() * error
                + i() * accumulatedError
                + d() * (error - lastError)
                + g() * cos(pos())
                + absF()
            )
            return (
                effort + relF() * error.sign
            ).coerceIn(-1.0, 1.0)
        }
}