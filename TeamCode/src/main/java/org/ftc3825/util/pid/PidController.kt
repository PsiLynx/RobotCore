package org.ftc3825.util.pid

class PidController(
    parameters: PIDFGParameters,
    override var setpointError: () -> Double,
    val apply: (Double) -> Unit,
    override var pos: () -> Double
): PIDFControllerImpl() {
    constructor(P: Double = 0.0,
                I: Number=0,
                D: Number = 0,
                absF: Number=0,
                relF: Number=0,
                G: Number=0,
                setpointError: () -> Double,
                apply: (Double) -> Unit,
                pos: () -> Double
    ): this(PIDFGParameters(P, I, D, absF, relF, G), setpointError, apply, pos)

    init {
        initializeController(parameters)
        error = 0.0
        lastError = 0.0
        accumulatedError = 0.0
    }

    override fun applyFeedback(feedback: Double) = apply(feedback)
}