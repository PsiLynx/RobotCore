package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.robotcore.external.Telemetry as RealTelemetry

object Telemetry: Subsystem<Telemetry> {
    override val components = arrayListOf<Component>()

    lateinit var telemetry: MultipleTelemetry

    var data = ArrayList<Pair<String, () -> Any>>()
    var lines = arrayListOf<() ->String>()

    fun initialize(dsTelem: RealTelemetry){
        telemetry = MultipleTelemetry(
            dsTelem,
            FtcDashboard.getInstance().telemetry
        )
    }

    fun addFunction(label: String, datum: () -> Any) = data.add( Pair(label, datum) )
    fun addLine(text: () -> String) = lines.add(text)
    fun addAll(builder: Telemetry.() -> Unit) {
        this.builder()
    }
    infix fun String.ids(other: () -> Any){
        addFunction(this, other)
    }
    fun newLine() = "\n".add()
    fun String.add() = addLine { this }

    override fun update(deltaTime: Double) {
        data.forEach { telemetry.addData(it.first, it.second().toString()) }
        telemetry.update()
    }

    override fun reset() {
        data = arrayListOf()
        lines = arrayListOf()
    }
}
