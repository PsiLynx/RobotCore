package org.firstinspires.ftc.teamcode.component

import java.util.function.DoubleSupplier
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import kotlin.math.PI

abstract class Encoder {

    abstract val posSupplier: DoubleSupplier
    open val velSupplier = { deltaT: Double ->
        (posSupplier.asDouble - lastPos) / deltaT
    }
    protected open val ticksPerRev: Double = 1.0
    protected open val wheelRadius: Double = 1 / ( 2 * PI )
    open var direction = FORWARD
    protected var currentPos = 0.0
    protected var lastPos = 0.0

    protected var offsetPos = 0.0

    var inPerTick = wheelRadius * 2 * PI / ticksPerRev

    open var pos: Double
        get() = ( currentPos * direction.dir + offsetPos ) * inPerTick
        set(newDist){
            offsetPos =
                 newDist / inPerTick - currentPos * direction.dir
        }

    fun linearVelocity(deltaTime: Double) =
        velSupplier(deltaTime) * inPerTick

    fun angularVelocity(deltaTime: Double) =
        velSupplier(deltaTime) / ticksPerRev

    open var angle: Double
        get() = (
            (
                (
                    ( currentPos * direction.dir + offsetPos ) / ticksPerRev
                ) % 1
            ) * 2 * PI
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
