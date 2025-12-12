package org.firstinspires.ftc.teamcode.component

import android.R.attr.value
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.ServoImplEx

class PWMLight(hardwareDevice: () -> ServoImplEx, port: Int): Servo(
    hardwareDevice,
    port
) {
    /**
     * do not use. does nothing.
     */
    override var position: Double
        get() = 0.0
        set(value) { }

    var color: Color = Color.OFF
        set(value) {
            field = value
            hardwareDevice.position = value.pos
        }

    enum class Color(val pos: Double){
        OFF(0.0),
        RED(0.277),
        ORANGE(0.333),
        GOLD(0.35),
        YELLOW(0.388),
        SAGE(0.444),
        GREEN(0.5),
        AZURE(0.555),
        BLUE(0.611),
        INGIGO(0.666),
        VIOLET(0.722),
        WHITE(1.0)
    }
}