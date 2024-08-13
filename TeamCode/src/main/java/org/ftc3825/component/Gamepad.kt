package org.ftc3825.component

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.internal.Trigger

class Gamepad(name: String, hardwareMap: HardwareMap) {

    val gamepad: Gamepad = hardwareMap.get(Gamepad::class.java, name)

    val a
        get() = Trigger { gamepad.a }
    val b
        get() = Trigger { gamepad.b }
    val x
        get() = Trigger { gamepad.x }
    val y
        get() = Trigger { gamepad.y }

    val left_bumper
        get() = Trigger { gamepad.left_bumper }
    val right_bumper
        get() = Trigger { gamepad.right_bumper }

    val dpad_left
        get() = Trigger { gamepad.dpad_left }
    val dpad_right
        get() = Trigger { gamepad.dpad_right }
    val dpad_up
        get() = Trigger { gamepad.dpad_up }
    val dpad_down
        get() = Trigger { gamepad.dpad_down }

    val start
        get() = Trigger { gamepad.start }
    val back
        get() = Trigger { gamepad.back }
    val guide
        get() = Trigger { gamepad.guide }

}