package org.ftc3825.subsystem

import org.ftc3825.fakehardware.FakeHardwareMap
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.ftc3825.component.Motor
import org.ftc3825.fakehardware.FakeTelemetry
import org.ftc3825.command.internal.CommandScheduler

object Telemetry: Subsystem<org.ftc3825.subsystem.Telemetry>() {
    override val motors = arrayListOf<Motor>()

    lateinit var telemetry: Telemetry

    var data = mutableMapOf<String, () -> Any>()
    var lines = arrayListOf<() ->String>()

    fun init(hardwareMap: HardwareMap) {
        if(hardwareMap is FakeHardwareMap){
            telemetry = FakeTelemetry()
        }
    }

    fun addData(label: String, datum: () -> Any){
        data[label] = datum
    }

    fun addLine(text: () -> String){
        lines.add(text)
    }


    override fun update(deltaTime: Double) {
        data.forEach {
            telemetry.addData(it.key, it.value.invoke().toString())
        }
        lines.forEach { telemetry.addLine(it.invoke()) }
        telemetry.update()
    }
}
