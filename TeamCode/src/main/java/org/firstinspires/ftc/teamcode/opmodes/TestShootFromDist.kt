package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Kicker
import org.firstinspires.ftc.teamcode.subsystem.Shooter
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import kotlin.math.PI

@TeleOp()
class TestShootFromDist: CommandOpMode() {
    override fun initialize() {
        Drivetrain.position = Pose2D(0.0, 0.0, PI / 2)
        Drivetrain.justUpdate().schedule()
        driver.a.onTrue(
            Shooter.shootingState { -Drivetrain.position.y }
            parallelTo (
                WaitUntilCommand { Shooter.readyToShoot }
                andThen Kicker.close()
                andThen WaitCommand(1)
                andThen Kicker.open()
                andThen WaitCommand(1)
            )
        )
    }
}