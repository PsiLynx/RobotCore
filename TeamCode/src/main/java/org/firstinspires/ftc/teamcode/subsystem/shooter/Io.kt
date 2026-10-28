package org.firstinspires.ftc.teamcode.subsystem.shooter

import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.subsystem.internal.SubsystemIo
import org.firstinspires.ftc.teamcode.util.degrees

interface Io: SubsystemIo {
    var hoodTargetAngle: Double

    var flywheelTargetState: VaState<State.DoubleState>
    fun flywheelSetPower(power: Double)
    var flywheelUsingFeedback: Boolean

    var turretTargetState: PvState<Rotation2D>
    var turretUsingFeedback: Boolean

    val turretLowerBound: Rotation2D
    val turretUpperBound: Rotation2D

    val hoodMinAngle: Double
    val hoodMaxAngle: Double

}
