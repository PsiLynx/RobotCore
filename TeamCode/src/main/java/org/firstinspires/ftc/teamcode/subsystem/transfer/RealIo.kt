package org.firstinspires.ftc.teamcode.subsystem.transfer

import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.RealSubsystemIo
import org.firstinspires.ftc.teamcode.util.log

class RealIo : RealSubsystemIo(), Io {

    private val motor = HardwareMap.transfer(Component.Direction.REVERSE)
    private val blocker = HardwareMap.blocker()

    init {
        motor.encoder = HardwareMap.transferEncoder(
            Component.Direction.REVERSE,
            1.0
        )
        motor.encoder!!.inPerTick = 1.0
    }

    override fun setPower(pow: Double) {
        motor.power = pow
    }

    override fun setBlockerPos(pos: Component.Opening) {
        if( pos == Component.Opening.OPEN){
            blocker.position = 0.20
        }
        else {
            blocker.position = 0.05
        }
    }

    override val components = listOf(motor)

    override fun update(deltaTime: Double) {
        super.update(deltaTime)
        Transfer.log("power") value motor.power
        Transfer.log("velocity") value motor.angularVelocity
        Transfer.log("acceleration") value motor.acceleration

        Transfer.log("blocker") value blocker.position

        Transfer.log("vel") value motor.velocity
        Transfer.log("vel ticks") value motor.encoder!!.velSupplier(0.0)
    }
}