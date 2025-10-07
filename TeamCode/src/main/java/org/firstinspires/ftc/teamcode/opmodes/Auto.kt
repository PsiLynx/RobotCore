package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Kicker
import org.firstinspires.ftc.teamcode.subsystem.Shooter
import org.firstinspires.ftc.teamcode.util.Globals

@Autonomous
class Auto: CommandOpMode() {
    override fun initialize() {
        Kicker.servo.position = 0.12

        (
            WaitCommand(0.1)
            andThen (
                    Flywheel.shootingState {
                    (Drivetrain.position - Globals.goalPose).mag
                }
            )
            andThen ( Flywheel.setPower(0.0) withTimeout 1 )
        ).schedule()
    }
}