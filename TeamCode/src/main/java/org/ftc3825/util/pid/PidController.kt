package org.ftc3825.util.pid

class PidController(
    val parameters: PIDFGParameters,
    val setpointError: () -> Double,
    val apply: (Double) -> Unit
): PIDFControllerImpl() {
    override fun getSetpointError() = setpointError()

    override fun applyFeedback(feedback: Double) = apply(feedback)
}