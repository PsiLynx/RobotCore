package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.subsystem.shooter.Shooter

@TeleOp(group = "a")
class HoodTest: CommandOpMode() {
    override fun postSelector() {
        driver.apply {
            a.onTrue(Shooter.run { io.hoodTargetAngle = 20.0 })
            b.onTrue(Shooter.run { io.hoodTargetAngle = 40.0 })
            y.onTrue(Shooter.run { io.hoodTargetAngle = 60.0 })
        }
    }

}