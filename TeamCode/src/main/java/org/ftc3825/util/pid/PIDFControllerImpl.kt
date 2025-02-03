package org.ftc3825.util.pid

abstract class PIDFControllerImpl: PIDFController {
    override lateinit var p: () -> Double
    override lateinit var i: () -> Double
    override lateinit var d: () -> Double
    override lateinit var f: () -> Double
    override var error = 0.0
    override var lastError = 0.0
    override var accumulatedError = 0.0

}