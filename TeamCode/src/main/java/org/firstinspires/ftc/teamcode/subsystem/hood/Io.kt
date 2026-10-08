package org.firstinspires.ftc.teamcode.subsystem.hood

import org.firstinspires.ftc.teamcode.subsystem.internal.Subsystem
import org.firstinspires.ftc.teamcode.subsystem.internal.SubsystemIo

interface Io: SubsystemIo {
    var servoPos: Double
}