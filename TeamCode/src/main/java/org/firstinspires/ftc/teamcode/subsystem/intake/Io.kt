package org.firstinspires.ftc.teamcode.subsystem.intake

import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.subsystem.internal.SubsystemIo

interface Io : SubsystemIo {
    fun setPower(pow: Double)
    var blockerPos: Double
}