package org.ftc3825.component

import java.util.function.DoubleSupplier

abstract class Encoder(){

    open var reversed = 1
    protected abstract val supplier: DoubleSupplier
    private var currentTicks = 0.0
    private var lastTicks = 0.0

    private var offsetTicks = 0.0

    var distance: Double
        get() = (currentTicks + offsetTicks) * reversed
        set(newDist){
            offsetTicks += - distance + newDist
        }
    val delta: Double
        get() = (currentTicks - lastTicks) * reversed

    fun update(deltaTime: Double) {
        lastTicks = currentTicks
        currentTicks = supplier.asDouble
    }

    fun resetPosition(){
        offsetTicks = - supplier.asDouble
    }
}
