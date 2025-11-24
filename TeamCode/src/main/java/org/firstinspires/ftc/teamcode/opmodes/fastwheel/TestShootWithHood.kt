package org.firstinspires.ftc.teamcode.opmodes.fastwheel

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.ShootingState
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.Kicker
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.SelectorInput
import org.firstinspires.ftc.teamcode.util.degrees
import kotlin.math.PI

@TeleOp()
class TestShootWithHood: CommandOpMode() {
    override fun postSelector() {
        Drivetrain.position = Pose2D(-50, 50, PI / 2 + degrees(50))
        Drivetrain.justUpdate().schedule()
        driver.a.whileTrue(
            ShootingState(
                { Drivetrain.position.vector },
            )

            parallelTo (
                WaitUntilCommand { Robot.readyToShoot }
            )
        )
    }
}