package org.firstinspires.ftc.teamcode.component.controller

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.TimedCommand
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.command.internal.Trigger
import java.time.Instant
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

class Gamepad(val gamepad: Gamepad) {

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

    val leftStick = Joystick(
        GamepadAxis { gamepad.left_stick_x.toDouble() },
        GamepadAxis { gamepad.left_stick_y.toDouble() },
        Trigger     { gamepad.left_stick_button       }
    )
    val rightStick = Joystick(
        GamepadAxis { gamepad.right_stick_x.toDouble() },
        GamepadAxis { gamepad.right_stick_y.toDouble() },
        Trigger     { gamepad.right_stick_button       }
    )

    val leftTrigger = GamepadTrigger { gamepad.left_trigger.toDouble() }
    val rightTrigger = GamepadTrigger { gamepad.right_trigger.toDouble() }

    fun rumble(time: Double = 0.5) = InstantCommand {
        gamepad.rumble((time * 1000).toInt())
    }

}
