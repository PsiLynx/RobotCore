package org.ftc3825.util.pid

class PidController(
    parameters: PIDFGParameters,
    val setpointError: () -> Double,
    val apply: (Double) -> Unit
): PIDFControllerImpl() {
    constructor(P: Double = 0.0,
                I: Number=0,
                D: Number = 0,
                F: Number=0,
                G: Number=0,
                setpointError: () -> Double,
                apply: (Double) -> Unit
    ): this(PIDFGParameters(P, I, D, F, G), setpointError, apply)

    init {
        initializeController(parameters)
        error = 0.0
        lastError = 0.0
        accumulatedError = 0.0
    }

    override fun getSetpointError() = setpointError()

    override fun applyFeedback(feedback: Double) = apply(feedback)
}