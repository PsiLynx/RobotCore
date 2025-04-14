package org.firstinspires.ftc.teamcode.controller

abstract class State <T> {
    abstract operator fun times(other: Number): State<T>
    abstract operator fun plus(other: State<T>): State<T>

    open operator fun unaryMinus()           = this * -1
    open operator fun div(other: Number)     = this * ( 1 / other.toDouble() )
    open operator fun minus(other: State<T>) = this + ( - other )


    class DoubleState(val value: Double): State<Double>() {
        override fun times(other: Number) = DoubleState(
            value * other.toDouble()
        )
        override fun plus(other: State<Double>) = DoubleState(
            value + ( other as DoubleState ).value
        )
    }

}