package org.ftc3825.subsystem

import org.ftc3825.component.Component
import org.firstinspires.ftc.robotcore.external.Telemetry as RealTelemetry

object Telemetry: Subsystem<Telemetry> {
    override val components = arrayListOf<Component>()

    lateinit var telemetry: RealTelemetry

    var data = ArrayList<Pair<String, () -> Any>>()
    var lines = arrayListOf<() ->String>()

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
