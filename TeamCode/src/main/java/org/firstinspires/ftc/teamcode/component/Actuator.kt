package org.firstinspires.ftc.teamcode.component

import com.acmerobotics.dashboard.DashboardCore
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.ValueProvider
import org.firstinspires.ftc.teamcode.util.Globals

interface Actuator: Component, ValueProvider<Double> {
    override fun get() = lastWrite or 0.0

    fun addToDash(category: String, name: String) =
        if(Globals.state == Globals.State.Running) {
            FtcDashboard.getInstance().addConfigVariable(category, name, this)
        } else Unit

}