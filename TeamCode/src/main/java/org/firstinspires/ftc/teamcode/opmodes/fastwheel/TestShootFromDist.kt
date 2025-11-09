package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Kicker

@TeleOp()
class TestShootFromDist: CommandOpMode() {
    override fun postSelector() {
        //Drivetrain.position = Pose2D(0.0, 0.0, PI / 2)
        Drivetrain.justUpdate().schedule()
        driver.a.whileTrue(
            Flywheel.shootingState { -Drivetrain.position.y }
        )
    }
}