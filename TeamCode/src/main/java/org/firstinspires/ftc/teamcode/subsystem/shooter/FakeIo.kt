package org.firstinspires.ftc.teamcode.subsystem.shooter

import org.firstinspires.ftc.teamcode.controller.PvState
import org.firstinspires.ftc.teamcode.controller.State
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Rotation2D
import org.firstinspires.ftc.teamcode.util.CacheValue

class FakeIo: Io {
    override var hoodTargetAngle: Double
        get() = TODO("Not yet implemented")
        set(value) {}
    override var flywheelTargetState: VaState<State.DoubleState>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var positionEstimate: CacheValue<Pose2D>
        get() = TODO("Not yet implemented")
        set(value) {}
    override val flywheelCurrentState: VaState<State.DoubleState>
        get() = TODO("Not yet implemented")

    override fun flywheelSetPower(power: Double) {
        TODO("Not yet implemented")
    }

    override var flywheelUsingFeedback: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var turretTargetState: PvState<Rotation2D>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var turretUsingFeedback: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override val turretLowerBound: Rotation2D
        get() = TODO("Not yet implemented")
    override val turretUpperBound: Rotation2D
        get() = TODO("Not yet implemented")
    override val hoodMinAngle: Double
        get() = TODO("Not yet implemented")
    override val hoodMaxAngle: Double
        get() = TODO("Not yet implemented")

    override fun update(deltaTime: Double) {
        TODO("Not yet implemented")
    }

    override fun reset() {
        TODO("Not yet implemented")
    }
}