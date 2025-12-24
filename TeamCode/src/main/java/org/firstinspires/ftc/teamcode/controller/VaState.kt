package org.firstinspires.ftc.teamcode.controller

import org.firstinspires.ftc.robotcore.external.navigation.Velocity

data class VaState<T: State<T>>(
    val velocity: T,
    val acceleration: T,
): State<VaState<T>> {
    override val mag get() = velocity.mag + acceleration.mag

    override fun nullState() = VaState<T>(
        velocity.nullState(),
        acceleration.nullState()
    )

    override fun times(other: Number) = VaState(
        velocity * other,
        acceleration * other,
    )

    override fun plus(other: VaState<T>) = VaState(
        velocity + other.velocity,
        acceleration + other.acceleration,
    )


    fun applyPD(
        p: Number,
        d: Number,
    ) = velocity * p - acceleration * d

    companion object {
        operator fun invoke(velocity: Double, acceleration: Double) = VaState(
            State.DoubleState(velocity),
            State.DoubleState(acceleration),
        )
    }
}