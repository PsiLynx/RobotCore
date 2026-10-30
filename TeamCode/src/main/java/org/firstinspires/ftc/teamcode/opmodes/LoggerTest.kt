package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.geometry.ChassisSpeeds
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import kotlin.random.Random

@TeleOp(group = "a")
class LoggerTest: CommandOpMode() {
    override fun postSelector(){
        Drivetrain.reset()
        Drivetrain.run {
            it.io.drivePowers(
                ChassisSpeeds(
                    Random.nextDouble(),
                    Random.nextDouble(),
                    Random.nextDouble(),
                )
            )
        }.schedule()
    }
}