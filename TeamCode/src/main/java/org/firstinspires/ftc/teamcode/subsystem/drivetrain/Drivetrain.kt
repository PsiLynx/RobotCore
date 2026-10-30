package org.firstinspires.ftc.teamcode.subsystem.drivetrain

import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI

object Drivetrain : Subsystem<Drivetrain, Io>(RealIo(), FakeIo()) {
    val MAX_VELOCITY: Double = 78.0
    val MAX_HEADING_VELOCITY: Double = 4*PI
    val MAX_ACCELERATION: Double = 90.0
    val MAX_HEADING_ACCELERATION: Double = 4*PI

    override fun update(deltaTime: Double) {
        log("position") value io.position
        log("velocity") value io.velocity
        log("acceration") value io.acceleration
    }
}