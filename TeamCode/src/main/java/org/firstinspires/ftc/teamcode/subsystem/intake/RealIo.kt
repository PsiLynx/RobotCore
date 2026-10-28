package org.firstinspires.ftc.teamcode.subsystem.intake

import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.RealSubsystemIo
import org.firstinspires.ftc.teamcode.util.log
import kotlin.compareTo
import kotlin.math.abs

class RealIo : RealSubsystemIo(), Io {

    private val intake1 = HardwareMap.intake1(FORWARD)
    private val intake2 = HardwareMap.intake2(REVERSE)

    override fun setPower(pow: Double) {
        intake1.power = pow
        intake2.power = pow
    }

    //block: 0.63 open: 0.73
    private val blocker = HardwareMap.blocker()

    override var blockerPos: Double
        get() = blocker.position
        set(value: Double) {
            blocker.position = value
        }

    override val components = listOf(intake1, blocker)

    init {
        intake1.encoder = HardwareMap.intakeEncoder(FORWARD, 28*3.0, 1.0)
    }

    override fun update(deltaTime: Double) {
        super.update(deltaTime)
        log("power") value intake1.power
        log("velocity") value intake1.angularVelocity
        log("accelaration") value intake1.acceleration

        log("blocker") value (
                if(blocker.position ==  0.1) "OPEN"
                else "CLOSED"
                )
    }
}