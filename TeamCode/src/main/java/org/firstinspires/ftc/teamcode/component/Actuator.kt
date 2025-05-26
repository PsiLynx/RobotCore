package org.firstinspires.ftc.teamcode.component

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.ValueProvider
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.abs
import kotlin.properties.Delegates

abstract class Actuator(
    val basePriority: Double, val priorityScale: Double
): Component(), ValueProvider<Double> {
    var timeTargetChanged = Optional.empty<Long>()

    var lastWrite = Optional.empty<Double>()
    var targetWrite: Optional<Double> by Delegates.observable(Optional
        .empty<Double>())
    { _, _, _ ->
        if(timeTargetChanged.equals(0L)) {
            timeTargetChanged = Optional(System.nanoTime())
        }

    }

    abstract fun doWrite(write: Optional<Double>)

    // for value provider
    override fun get() = lastWrite or 0.0

    val timePriority: Double get() = (
        (System.nanoTime() - (timeTargetChanged or 0L) )
        * 1e-6
        * priorityScale
    )

    override var priority: Double = 0.0
        get() =
            if(targetWrite.empty) 0.0
            else if (timeTargetChanged.equals(0L)) basePriority
            else if(lastWrite.empty) basePriority + timePriority
            else (
                ( basePriority + timePriority )
                * abs(
                      (lastWrite or 0.0)
                    - (targetWrite or 0.0)
                )
            )

    override fun ioOp() {
        timeTargetChanged = Optional(0L)
        doWrite(targetWrite)
        lastWrite = targetWrite
    }
    fun addToDash(category: String, name: String) =
        if(Globals.state == Globals.State.Running) {
            FtcDashboard.getInstance().addConfigVariable(category, name, this)
        } else Unit

}