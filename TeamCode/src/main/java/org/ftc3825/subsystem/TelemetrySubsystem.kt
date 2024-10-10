package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.ftc3825.component.Motor
import org.tensorflow.lite.support.label.TensorLabel

object TelemetrySubsystem: Subsystem<TelemetrySubsystem> {
    override var initialized = false
    override val motors = arrayListOf<Motor>()

    lateinit var telemetry: Telemetry

    var data = mutableMapOf<String, () -> Any>()
    var lines = arrayListOf<() ->String>()

    fun addData(label: String, datum: () -> Any){
        data[label] = datum
    }

    fun addLine(text: () -> String){
        lines.add(text)
    }

    override fun init(hardwareMap: HardwareMap) { }

    override fun update(deltaTime: Double) {
        data.forEach {
            telemetry.addData(it.key, it.value.invoke().toString())
        }
        lines.forEach { telemetry.addLine(it.invoke()) }
        telemetry.update()
    }
}