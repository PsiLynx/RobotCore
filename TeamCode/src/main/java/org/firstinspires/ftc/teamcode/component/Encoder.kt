package org.firstinspires.ftc.teamcode.component

import java.util.function.DoubleSupplier
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD

abstract class Encoder {

    open var direction = FORWARD
    abstract val posSupplier: DoubleSupplier
    protected var currentPos = 0.0
    protected var lastPos = 0.0

    protected var offsetPos = 0.0

    open var pos: Double
        get() = currentPos * direction.dir + offsetPos
        set(newDist){
            offsetPos = - currentPos * direction.dir + newDist
        }
    open val delta: Double
        get() = (currentPos - lastPos) * direction.dir

    open fun update(deltaTime: Double) {
        lastPos = currentPos
        currentPos = posSupplier.asDouble
    }

    fun resetPosition(){ offsetPos = - posSupplier.asDouble }
}
