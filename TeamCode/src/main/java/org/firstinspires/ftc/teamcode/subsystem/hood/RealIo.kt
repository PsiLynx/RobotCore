package org.firstinspires.ftc.teamcode.subsystem.hood

import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Servo.Range
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.RealSubsystemIo

class RealIo: Io, RealSubsystemIo() {
    private val servo = HardwareMap.hood(range = Range.Default)
    override val components = listOf(servo)

    override var servoPos: Double
        get() = servo.position
        set(value) {
            servo.position = value
        }
}