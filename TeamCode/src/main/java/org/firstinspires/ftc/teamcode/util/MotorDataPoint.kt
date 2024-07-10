package org.firstinspires.ftc.teamcode.util

data class MotorDataPoint(
    val voltage: Double,
    val velocity: Double,
    val output: Double
) : FunctionOutput(
    output = output,
    inputs = doubleArrayOf(
        voltage,
        velocity
    )
)