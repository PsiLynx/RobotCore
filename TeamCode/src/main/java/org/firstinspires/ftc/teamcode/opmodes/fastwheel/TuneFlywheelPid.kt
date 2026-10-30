package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter

@TeleOp(group = "a")
class TuneFlywheelPid: CommandOpMode() {
    override fun postSelector() {
        Shooter.run {
            io.flywheelTargetState = VaState(
                driver.leftStick.y.toDouble() * 200,
                0.0
            )
            io.flywheelUsingFeedback = true
        }.schedule()
    }
}