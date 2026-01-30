package org.firstinspires.ftc.teamcode.opmodes.dt

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain

@TeleOp
class TuneDtHeadingLock: CommandOpMode() {
    override fun postSelector() {
        driver.y.onTrue(
            TankDrivetrain.headingLock(2.0)
        )
        driver.a.onTrue(
            TankDrivetrain.headingLock(-2.0)
        )
        RunCommand { Thread.sleep(10) }.schedule()
    }
}