package org.firstinspires.ftc.teamcode.command

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.geometry.Rotation2D

class Cycle(
    val startHeading: Rotation2D,
    val command: (Rotation2D) -> Command,
){
    constructor(startHeading: Double, command: (Rotation2D) -> Command): this(
        Rotation2D(startHeading), command
    )

    operator fun invoke(nextStartHeading: Rotation2D) = command(
        nextStartHeading
    )
    operator fun invoke(nextStartHeading: Double) = command(
        Rotation2D(nextStartHeading)
    )
}