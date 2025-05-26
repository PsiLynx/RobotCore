package org.firstinspires.ftc.teamcode.component

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.ValueProvider
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.abs
import kotlin.properties.Delegates

abstract class Actuator(
    val basePriority: Double, val priorityScale: Double
): Component(), ValueProvider<Double> {
    var timeTargetChanged = Optional.empty<Double>()

    var lastWrite = Optional.empty<Double>()
    var targetWrite: Optional<Double> by Delegates.observable(Optional
        .empty<Double>())
    { thisVal, old, new ->
        if(timeTargetChanged.empty) {
            timeTargetChanged = Optional(Globals.currentTime)
        }

    }

    abstract fun doWrite(write: Optional<Double>)

    // for value provider
    override fun get() = lastWrite or 0.0

    val timePriority: Double get() = (
        (Globals.currentTime - (timeTargetChanged or Globals.currentTime) )
        * priorityScale
    )

    override var priority: Double
        get() {
            val output = (
                if (targetWrite.empty) 0.0
                else if (lastWrite.empty) basePriority + timePriority
                else (
                    (basePriority + timePriority)
                    * abs(
                        (lastWrite or 0.0)
                        - (targetWrite or 0.0)
                    )
                )
            )
            if(output.isNaN()) error("")
            return output
        }
        set(value) { }

    override fun ioOp() {
        timeTargetChanged = Optional.empty()
        doWrite(targetWrite)
        lastWrite = targetWrite
    }
    fun addToDash(category: String, name: String) =
        if(Globals.state == Globals.State.Running) {
            FtcDashboard.getInstance().addConfigVariable(category, name, this)
        } else Unit

}