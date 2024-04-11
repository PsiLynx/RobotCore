package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.DcMotor

class Encoder(motor: DcMotor, ticksPerRevolution: Double, wheelRadius: Double = 0.0, gearRatio: Double = 1.0, offset: Double = 0.0) {
    var ticksPerRevolution: Double = ticksPerRevolution
    var wheelRadius: Double = wheelRadius
    var gearRatio: Double = gearRatio
    var offset: Double = offset
    var motor: DcMotor = motor

    var angle: Double
        get() = ((motor.currentPosition + offset) / ticksPerRevolution) % 360
        set(newAngle: Double):Unit{offset = (newAngle - angle) / 360.0 * ticksPerRevolution}

    var distance: Double
        get() = ((motor.currentPosition + offset) / ticksPerRevolution) * wheelRadius / gearRatio
        set(newDist: Double):Unit{offset = newDist - distance * ticksPerRevolution * gearRatio / wheelRadius}
}