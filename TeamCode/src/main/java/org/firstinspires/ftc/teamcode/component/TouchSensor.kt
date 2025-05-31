package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.logging.Input

class TouchSensor(private val port: Int, val default: Boolean = false) {
    val pressed: Boolean
        get() = ( HWManager.BulkData.digital[port] == 1.0 ) xor default

    val status: String
        get() = if(pressed) "pressed" else "not pressed"
}