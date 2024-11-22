package org.ftc3825.util.pid

interface PIDFController {

    var p: Double
    var i: Double
    var d: Double
    var f: Double

    var lastError: Double
    var error: Double

    var accumulatedError: Double

    fun initializeController(parameters: PIDFGParameters) {
        p = parameters.P.toDouble()
        i = parameters.I.toDouble()
        d = parameters.D.toDouble()
        f = parameters.F.toDouble()

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
        updateError(deltaTime)

        applyFeedback(feedback)
    }

    fun updateError(deltaTime: Double) {
        lastError = error
        error = getSetpointError()

        accumulatedError += error * deltaTime
    }

    fun resetController() {
        lastError = getSetpointError()
        error = getSetpointError()
        accumulatedError = 0.0
    }

    val feedback: Double
        get() = (
                  p * error
                + i * accumulatedError
                + d * ( error - lastError )
                + f
        ). coerceIn(-0.3, 1.0)
}