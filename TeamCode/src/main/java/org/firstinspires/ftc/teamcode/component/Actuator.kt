package org.firstinspires.ftc.teamcode.component

import com.acmerobotics.dashboard.DashboardCore
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.ValueProvider

interface Actuator: Component, ValueProvider<Double> {
    override fun get() = lastWrite or 0.0

    fun addToDash(category: String, name: String) =
        FtcDashboard.getInstance().addConfigVariable(category, name, this)

}