package org.firstinspires.ftc.teamcode.controller

class PvState<T: State<T>>(
    val position: T,
    val velocity: T,
): State<PvState<T>> {
    override fun nullState() = PvState<T>(
        position.nullState(),
        velocity.nullState()
    )
    constructor(position: Double, velocity: Double): this(
        State.DoubleState(position) as T,
        State.DoubleState(velocity) as T
    )
    override fun times(other: Number) = PvState(
        position * other,
        velocity * other,
    )

    override fun plus(other: PvState<T>) = PvState(
        position + other.position,
        velocity + other.velocity,
    )
    fun applyPD(
        p: Number,
        d: Number,
    ) = position * p - velocity * d
}