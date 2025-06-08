package org.firstinspires.ftc.teamcode.controller

abstract class State <T> {
    val value: T get() = this as T
    abstract operator fun times(other: Number): State<T>
    abstract operator fun plus(other: State<T>): State<T>

    open operator fun unaryMinus()           = this * -1
    open operator fun div(other: Number)     = this * ( 1 / other.toDouble() )
    open operator fun minus(other: State<T>) = this + ( - other )


    class DoubleState(val number: Double): State<Double>() {
        override fun times(other: Number) = DoubleState(
            number * other.toDouble()
        )
        override fun plus(other: State<Double>) = DoubleState(
            number + ( other as DoubleState ).number
        )
    }

}