package org.firstinspires.ftc.teamcode.controller

import org.firstinspires.ftc.robotcore.external.navigation.Velocity

data class PvaState<T: State<T>>(
    val position: T,
    val velocity: T,
    val acceleration: T,
): State<PvaState<T>> {
    override val mag get() = position.mag + velocity.mag + acceleration.mag

    val pvState get() = PvState(position, velocity)
    val vaState get() = PvState(velocity, acceleration)

    override fun nullState() = PvaState<T>(
        position.nullState(),
        velocity.nullState(),
        acceleration.nullState(),
    )
    override fun times(other: Number) = PvaState(
        position * other,
        velocity * other,
        acceleration * other,
    )

    override fun plus(other: PvaState<T>) = PvaState(
        position + other.position,
        velocity + other.velocity,
        acceleration + other.acceleration,
    )

    companion object {
        operator fun invoke(
            position: Double,
            velocity: Double,
            acceleration: Double,
        ) = PvaState(
            State.DoubleState(position),
            State.DoubleState(velocity),
            State.DoubleState(acceleration),
        )
    }
}