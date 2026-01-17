package org.firstinspires.ftc.teamcode.controller

import org.firstinspires.ftc.robotcore.external.navigation.Velocity

data class PvaState<T: State<T>>(
    val position: T,
    val velocity: T,
): State<PvaState<T>> {
    override val mag get() = position.mag + velocity.mag

    override fun nullState() = PvaState<T>(
        position.nullState(),
        velocity.nullState()
    )
    override fun times(other: Number) = PvaState(
        position * other,
        velocity * other,
    )

    override fun plus(other: PvaState<T>) = PvaState(
        position + other.position,
        velocity + other.velocity,
    )

    fun applyPD(
        p: Number,
        d: Number,
    ) = position * p - velocity * d

    companion object {
        operator fun invoke(position: Double, velocity: Double) = PvaState(
            State.DoubleState(position),
            State.DoubleState(velocity),
        )
    }
}