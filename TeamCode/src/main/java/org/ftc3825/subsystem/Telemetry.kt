package org.ftc3825.subsystem

import org.ftc3825.fakehardware.FakeHardwareMap
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry as RealTelemetry
import org.ftc3825.component.Motor
import org.ftc3825.fakehardware.FakeTelemetry
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.component.Component

object Telemetry: Subsystem<org.ftc3825.subsystem.Telemetry> {
    override val components = arrayListOf<Component>()

    lateinit var telemetry: RealTelemetry

    var data = ArrayList<Pair<String, () -> Any>>()
    var lines = arrayListOf<() ->String>()

    fun addFunction(label: String, datum: () -> Any){
        data.add( Pair(label, datum) )
    }

    fun addLine(text: () -> String){
        lines.add(text)
    }


    override fun update(deltaTime: Double) {
        data.forEach { 
            telemetry.addData(it.first, it.second().toString())
        }
        telemetry.update()
    }
}
