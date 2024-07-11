package org.firstinspires.ftc.teamcode.fakehardware

import com.qualcomm.robotcore.hardware.Gamepad

class FakeGamepad: FakeHardware, Gamepad() {
    fun setState(button: String, value: Boolean){
        when(button){
            "a"            -> a            = value
            "b"            -> b            = value
            "x"            -> x            = value
            "y"            -> y            = value
            "back"         -> back         = value
            "start"        -> start        = value
            "options"      -> options      = value
            "dpad_up"      -> dpad_up      = value
            "dpad_down"    -> dpad_down    = value
            "dpad_left"    -> dpad_left    = value
            "dpad_right"   -> dpad_right   = value
            "left_bumper"  -> left_bumper  = value
            "right_bumper" -> right_bumper = value

        }
    }

    fun press   (button: String) = setState(button, true)
    fun depress (button: String) = setState(button, false)

    override fun update(deltaTime: Double) { }
}