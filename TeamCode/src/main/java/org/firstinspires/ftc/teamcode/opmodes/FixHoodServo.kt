package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystem.Hood

@TeleOp(group = "a")
class FixHoodServo: CommandOpMode() {
    override fun postSelector() {
        Hood.run {
            it.servo.position = 0.08
        }.schedule()
    }
}