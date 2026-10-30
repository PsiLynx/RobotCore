package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.intake.Intake
import org.firstinspires.ftc.teamcode.subsystem.intake.Io

@Autonomous(name = "Drive Forward")
class DriveForwardAuto: CommandOpMode() {
    override fun preSelector() {
        Intake.io.setFlowerPos(Io.FlowerPosition.UP)
    }
    override fun postSelector() {
        Drivetrain.run { it.io.drivePowers(ChassisSpeeds(vx = 0.25)) }
            .schedule()
    }
}
