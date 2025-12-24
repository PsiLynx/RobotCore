package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel

@TeleOp()
class TestShootFromDist: CommandOpMode() {
    override fun postSelector() {
        //TankDrivetrain.position = Pose2D(0.0, 0.0, PI / 2)
        TankDrivetrain.justUpdate().schedule()
        driver.a.whileTrue(
            Flywheel.shootingState { -TankDrivetrain.position.y }
        )
    }
}