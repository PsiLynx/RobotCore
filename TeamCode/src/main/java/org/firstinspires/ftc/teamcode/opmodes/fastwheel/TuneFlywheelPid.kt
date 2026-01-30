package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Flywheel

@TeleOp
class TuneFlywheelPid: CommandOpMode() {
    override fun postSelector() {
        Flywheel.run {
            it.targetState = VaState(
                driver.leftStick.y.toDouble() * 200,
                0.0
            )
            it.usingFeedback = true
        }.schedule()
    }
}