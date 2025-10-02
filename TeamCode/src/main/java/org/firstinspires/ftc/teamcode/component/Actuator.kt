package org.firstinspires.ftc.teamcode.component

import android.R.attr.value
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.ValueProvider
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.abs
import kotlin.properties.Delegates

abstract class Actuator(
    override val ioOpTime: Double,
    val basePriority: Double,
    val priorityScale: Double,
): Component(), ValueProvider<Double> {
    var timeTargetChanged = Optional.empty<Double>()

    abstract val port: Int

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
        * priorityScale / HWManager.targetLooptime
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
            if(output.isNaN()) error(
                "priority cannot be null. "
                + "base: $basePriority, time: $timePriority"
                + "last: $lastWrite, target: $targetWrite"
            )
            return output
        }
        set(value) { }

    override fun ioOp() {
        timeTargetChanged = Optional.empty()
        doWrite(targetWrite)
        lastWrite = targetWrite
    }
    fun addToDash(category: String) =
        if(Globals.running) {
            FtcDashboard.getInstance().addConfigVariable(
                category,
                "$port",
                this
            )
        } else Unit

}