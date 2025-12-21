package org.firstinspires.ftc.teamcode.subsystem

import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.util.log

object TestSubsystem: Subsystem<TestSubsystem>() {
    val digitalSensor = HardwareMap.pedal(
        trueValue = true,
        falseValue = false,
    )
    override val components = listOf<Component>()

    override fun update(deltaTime: Double) {
        log("value") value digitalSensor.value
    }

}