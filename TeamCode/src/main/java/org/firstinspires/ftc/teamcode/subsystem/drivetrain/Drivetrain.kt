package org.firstinspires.ftc.teamcode.subsystem.drivetrain

import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI

object Drivetrain : Subsystem<Drivetrain, Io>(RealIo(), FakeIo()) {
    const val maxVel = 78.0
    const val maxHeadingVel = 4*PI
    const val maxAccel = 140.0
    const val maxHeadingAccel = 4*PI

    override fun update(deltaTime: Double) {
        log("position") value io.position
        log("velocity") value io.velocity
        log("robotCentricVel") value io.robotCentricVelocity
        log("acceration") value io.acceleration
        log("powers/x") value io.powers.vx
        log("powers/y") value io.powers.vy
        log("powers/theta") value io.powers.vTheta
    }
}