package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DigitalChannel
import org.firstinspires.ftc.teamcode.subsystem.TestSubsystem

@TeleOp
class TestSubsystemUpdate: CommandOpMode() {
    override fun postSelector() {
        TestSubsystem.justUpdate().schedule()
    }
}