package org.firstinspires.ftc.teamcode.component

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.ValueProvider
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.abs
import kotlin.properties.Delegates

abstract class Actuator: Component(), ValueProvider<Double> {
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

    fun addToDash(category: String) =
        if(Globals.running) {
            FtcDashboard.getInstance().addConfigVariable(
                category,
                "$port",
                this
            )
        } else Unit

}