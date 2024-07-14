package org.firstinspires.ftc.teamcode.util

import kotlin.math.cos

interface PIDFController {

    var p: Double
    var i: Double
    var d: Double
    var f: Double
    var g: Double

    var lastError: Double
    var error: Double

    var accumulatedError: Double

    fun initializeController(parameters: PIDFGParameters) {
        p = parameters.P.toDouble()
        i = parameters.I.toDouble()
        d = parameters.D.toDouble()
        f = parameters.F.toDouble()
        g = parameters.G.toDouble()

        lastError = getSetpointError()
        error = getSetpointError()
    }

    /**
     * error as  ( reference point - current)
     * for angle error, use radians
     */
    fun getSetpointError(): Double
    fun applyFeedback(feedback: Double)

    fun updateController(deltaTime: Double) {
        lastError = error
        error = getSetpointError()

        accumulatedError += error * deltaTime

        applyFeedback(feedback)
    }

    private val feedback: Double
        get() = (
                  p * error
                + i * accumulatedError
                + d * ( error - lastError )
                + f
                + g * cos(error)
        )
}