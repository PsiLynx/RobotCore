package org.firstinspires.ftc.teamcode.component

import java.util.function.DoubleSupplier
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.logging.Input
import kotlin.math.PI

abstract class Encoder {

    abstract val posSupplier: DoubleSupplier
    protected open val ticksPerRev: Double = 1.0
    protected open val wheelRadius: Double = 1 / ( 2 * PI )
    open var direction = FORWARD
    protected var currentPos = 0.0
    protected var lastPos = 0.0

    protected var offsetPos = 0.0

    private val posScale: Double get() = wheelRadius * 2 * PI / ticksPerRev

    open var pos: Double
        get() = ( currentPos * direction.dir + offsetPos ) * posScale
        set(newDist){
            offsetPos =
                 newDist / posScale - currentPos * direction.dir
        }
    open val delta: Double
        get() = (currentPos - lastPos) * direction.dir * posScale

    open var angle: Double
        get() = (
            ( ( currentPos * direction.dir + offsetPos ) / ticksPerRev )
            % 1 * 2 * PI
        )
        set(value) {
            offsetPos =
                - currentPos * direction.dir + value * ticksPerRev / ( 2 * PI )
        }

    open fun update(deltaTime: Double) {
        lastPos = currentPos
        currentPos = posSupplier.asDouble
    }
    fun resetPosition(){ offsetPos = - posSupplier.asDouble }

}
