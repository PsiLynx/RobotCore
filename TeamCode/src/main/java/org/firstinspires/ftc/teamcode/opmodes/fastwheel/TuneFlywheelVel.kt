package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.InstantCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.FlywheelConfig

@TeleOp
class TuneFlywheelVel: CommandOpMode() {
    override fun postSelector() {
        Flywheel.run {
            it.controller.targetPosition =
                driver.leftStick.y.toDouble()
            it.usingFeedback = true
        }.schedule()
    }
}