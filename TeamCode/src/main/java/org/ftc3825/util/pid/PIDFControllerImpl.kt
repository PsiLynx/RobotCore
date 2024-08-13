package org.ftc3825.util.pid

abstract class PIDFControllerImpl: PIDFController {
    override var p = 0.0
    override var i = 0.0
    override var d = 0.0
    override var f = 0.0
    override var g = 0.0
    override var error = 0.0
    override var lastError = 0.0
    override var accumulatedError = 0.0

}