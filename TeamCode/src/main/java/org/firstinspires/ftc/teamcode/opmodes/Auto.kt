package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Kicker

@Autonomous
class Auto: CommandOpMode() {
    override fun initialize() {
        Kicker.servo.position = 0.6
        (
        WaitCommand(0.1) andThen
                (
                    Flywheel.setPower(0.7)
                    racesWith (
                    WaitCommand(1)
                            andThen Kicker.close()
                            andThen Kicker.open()
                            andThen Kicker.close()
                            andThen WaitCommand(1.5)
                    )
                )
            andThen ( Flywheel.setPower(0.0) withTimeout 1 )
            andThen (
                Drivetrain.run {
                    it.setWeightedDrivePower(-0.3, 0.0, 0.1)
                } withTimeout 3 withEnd {
                    Drivetrain.setWeightedDrivePower()
                }
            )
        ).schedule()
    }
}