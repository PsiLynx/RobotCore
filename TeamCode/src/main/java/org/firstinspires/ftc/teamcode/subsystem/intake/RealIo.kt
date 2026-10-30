package org.firstinspires.ftc.teamcode.subsystem.intake

import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.RealSubsystemIo
import org.firstinspires.ftc.teamcode.util.log
import kotlin.compareTo
import kotlin.math.abs

class RealIo : RealSubsystemIo(), Io {

    private val motor = HardwareMap.intake(FORWARD)
    private val flower = HardwareMap.flower()

    override fun setPower(pow: Double) {
        motor.power = pow
    }

    override fun setFlowerPos(flowerPosition: Io.FlowerPosition) {
        if(flowerPosition == Io.FlowerPosition.DOWN){
            flower.position = 0.1
        }
        else {
            flower.position = 0.9
        }
    }

    override val components = listOf(motor)

    override fun update(deltaTime: Double) {
        super.update(deltaTime)
        log("power") value motor.power
        log("velocity") value motor.angularVelocity
        log("accelaration") value motor.acceleration

    }
}