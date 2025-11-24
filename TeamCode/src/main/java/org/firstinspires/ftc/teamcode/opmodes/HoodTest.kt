package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystem.Hood

@TeleOp
class HoodTest: CommandOpMode() {
    override fun postSelector() {
        driver.apply {
            a.onTrue(Hood.setAngle(20.0))
            b.onTrue(Hood.setAngle(40.0))
            y.onTrue(Hood.setAngle(60.0))
        }
    }

}