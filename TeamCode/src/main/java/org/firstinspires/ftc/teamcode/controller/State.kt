package org.firstinspires.ftc.teamcode.controller

interface State <T: State<T>> {
    fun nullState(): T
    operator fun times(other: Number): T
    operator fun plus(other: T): T

    operator fun unaryMinus(): T
        = this * -1

    operator fun div(other: Number): T
        = this * ( 1 / other.toDouble() )

    operator fun minus(other: State<T>): T
        = this + ( - other )


    class DoubleState(val value: Double): State<DoubleState>, Number() {
        override fun nullState() = DoubleState(0.0)

        override fun times(other: Number) = DoubleState(
            value * other.toDouble()
        )
        override fun plus(other: DoubleState)
            = DoubleState(
                value + other.value
            )

        override fun toShort()  = value.toInt().toShort()
        override fun toByte()   = value.toInt().toByte()
        override fun toFloat()  = value.toFloat()
        override fun toLong()   = value.toLong()
        override fun toInt()    = value.toInt()
        override fun toDouble() = value
    }

}