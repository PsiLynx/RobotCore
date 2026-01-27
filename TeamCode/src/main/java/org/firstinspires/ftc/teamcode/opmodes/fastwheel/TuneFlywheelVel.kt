package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.controller.VaState
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig

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