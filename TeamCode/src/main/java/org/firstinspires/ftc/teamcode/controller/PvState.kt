package org.firstinspires.ftc.teamcode.controller

import org.firstinspires.ftc.robotcore.external.navigation.Velocity

data class PvState<T: State<T>>(
    val position: T,
    val velocity: T,
): State<PvState<T>> {
    override val mag get() = position.mag + velocity.mag

    override fun nullState() = PvState<T>(
        position.nullState(),
        velocity.nullState()
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

    companion object {
        operator fun invoke(position: Double, velocity: Double) = PvState(
            State.DoubleState(position),
            State.DoubleState(velocity),
        )
    }
}