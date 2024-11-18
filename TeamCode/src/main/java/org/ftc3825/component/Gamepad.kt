package org.ftc3825.component

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.internal.GlobalHardwareMap
import org.ftc3825.command.internal.Trigger
import kotlin.math.pow
import kotlin.math.sign

class Gamepad(val gamepad: Gamepad) {

    constructor(name: String) : this(GlobalHardwareMap.get(Gamepad::class.java, name))

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

    val left_stick_x
        get() = gamepad.left_stick_x
    val right_stick_x
        get() = gamepad.right_stick_x
    val left_stick_y
        get() = gamepad.left_stick_y
    val right_stick_y
        get() = gamepad.right_stick_y

    val left_stick_x_sq
        get() = gamepad.left_stick_x.pow(2) * gamepad.left_stick_x.sign
    val right_stick_x_sq
        get() = gamepad.right_stick_x.pow(2) * gamepad.right_stick_x.sign
    val left_stick_y_sq
        get() = gamepad.left_stick_y.pow(2) * gamepad.left_stick_y.sign
    val right_stick_y_sq
        get() = gamepad.right_stick_y.pow(2) * gamepad.right_stick_y.sign

    val left_trigger
        get() = gamepad.left_trigger
    val right_trigger
        get() = gamepad.right_trigger


}
