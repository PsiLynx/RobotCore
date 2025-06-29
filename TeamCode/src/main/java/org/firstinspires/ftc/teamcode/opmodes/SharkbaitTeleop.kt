package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.component.controller.Gamepad
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.SharkbaitDrivetrain

@TeleOp(name = "sharkbait")
class SharkbaitTeleop: CommandOpMode() {
    override fun initialize() {

        val driver = Gamepad(gamepad1!!)
        SharkbaitDrivetrain.run {
            it.setWeightedDrivePower(
                -driver.leftStick.y.sq,
                driver.leftStick.x.sq,
                -driver.rightStick.x.sq,
            )
        }.schedule()
    }
}
