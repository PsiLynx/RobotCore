package org.firstinspires.ftc.teamcode.subsystem.transfer

import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.RealSubsystemIo
import org.firstinspires.ftc.teamcode.util.log

class RealIo : RealSubsystemIo(), Io {

    private val motor = HardwareMap.transfer(Component.Direction.REVERSE)
    private val blocker = HardwareMap.blocker()

    override fun setPower(pow: Double) {
        motor.power = pow
    }

    override fun setBlockerPos(pos: Double) {
        blocker.position = pos
    }

    override val components = listOf(motor)

    override fun update(deltaTime: Double) {
        super.update(deltaTime)
        log("power") value motor.power
        log("velocity") value motor.angularVelocity
        log("accelaration") value motor.acceleration

    }
}