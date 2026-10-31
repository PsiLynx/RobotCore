package org.firstinspires.ftc.teamcode.subsystem.drivetrain

import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI

object Drivetrain : Subsystem<Drivetrain, Io>(RealIo(), FakeIo()) {
    const val maxVel: Double = 78.0
    const val maxHeadingVel: Double = 4*PI
    const val maxAccel: Double = 90.0
    const val maxHeadingAccel: Double = 4*PI

    override fun update(deltaTime: Double) {
        log("position") value io.position
        log("velocity") value io.velocity
        log("acceration") value io.acceleration
    }
}