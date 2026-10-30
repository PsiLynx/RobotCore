package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter

@TeleOp(group = "a")
class FixHoodServo: CommandOpMode() {
    override fun postSelector() {
        Shooter.run {
            it.io.hoodTargetAngle = 0.08
        }.schedule()
    }
}