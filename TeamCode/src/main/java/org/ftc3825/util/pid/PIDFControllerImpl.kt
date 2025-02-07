package org.ftc3825.util.pid

import kotlin.properties.Delegates

abstract class PIDFControllerImpl: PIDFController {
    override lateinit var p: () -> Double
    override lateinit var i: () -> Double
    override lateinit var d: () -> Double
    override lateinit var absF: () -> Double
    override lateinit var relF: () -> Double
    override lateinit var g: () -> Double
    override var error = 0.0
    override var lastError = 0.0
    override var accumulatedError = 0.0

}