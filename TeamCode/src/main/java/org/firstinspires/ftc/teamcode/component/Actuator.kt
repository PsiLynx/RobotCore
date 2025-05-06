package org.firstinspires.ftc.teamcode.component

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.ValueProvider
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.abs
import kotlin.properties.Delegates

abstract class Actuator(
    val basePriority: Double, val priorityScale: Double
): Component(), ValueProvider<Double> {
    var timeTargetChanged = 0L

    var lastWrite = Write.empty()
    var targetWrite: Write by Delegates.observable(Write.empty()) { _, _, _ ->
        timeTargetChanged = System.nanoTime()

    }

    abstract fun doWrite(write: Write)

    // for value provider
    override fun get() = lastWrite or 0.0

    val timePriority: Double get() = (
        (System.nanoTime() - timeTargetChanged)
        * 1e-6
        * priorityScale
    )

    override val priority: Double
        get() =
            if(targetWrite == Write.empty()) 0.0
            else if (timeTargetChanged == 0L) basePriority
            else if(lastWrite == Write.empty()) basePriority + timePriority
            else (
                ( basePriority + timePriority )
                * abs(
                      (lastWrite or 0.0)
                    - (targetWrite or 0.0)
                )
            )

    override fun ioOp() {
        timeTargetChanged = System.nanoTime()
        doWrite(targetWrite)
        lastWrite = targetWrite
    }
    fun addToDash(category: String, name: String) =
        if(Globals.state == Globals.State.Running) {
            FtcDashboard.getInstance().addConfigVariable(category, name, this)
        } else Unit

}