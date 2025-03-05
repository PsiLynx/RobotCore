package org.teamcode.component

import com.qualcomm.robotcore.hardware.Gamepad
import org.teamcode.command.internal.GlobalHardwareMap
import org.teamcode.command.internal.Trigger
import kotlin.math.abs
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

    val leftBumper
        get() = Trigger { gamepad.left_bumper }
    val rightBumper
        get() = Trigger { gamepad.right_bumper }

    val dpadLeft
        get() = Trigger { gamepad.dpad_left }
    val dpadRight
        get() = Trigger { gamepad.dpad_right }
    val dpadUp
        get() = Trigger { gamepad.dpad_up }
    val dpadDown
        get() = Trigger { gamepad.dpad_down }

    val start
        get() = Trigger { gamepad.start }
    val back
        get() = Trigger { gamepad.back }
    val guide
        get() = Trigger { gamepad.guide }

    val leftStickX
        get() = gamepad.left_stick_x
    val rightStickX
        get() = gamepad.right_stick_x
    val leftStickY
        get() = gamepad.left_stick_y
    val rightStickY
        get() = gamepad.right_stick_y

    private fun curve(stick: Float): Double{
        val output = ( stick.pow(2) * stick.sign ).toDouble()
        return if(abs(output) > 0.05) output else 0.0
    }
    val leftStickXSq: Double
        get() = curve(leftStickX)
    val rightStickXSq: Double
        get() = curve(rightStickX)
    val leftStickYSq: Double
        get() = curve(leftStickY)
    val rightStickYSq: Double
        get() = curve(rightStickY)

    val leftTrigger
        get() = gamepad.left_trigger
    val rightTrigger
        get() = gamepad.right_trigger


}
