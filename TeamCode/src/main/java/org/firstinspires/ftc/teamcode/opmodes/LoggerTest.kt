package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import kotlin.random.Random

@TeleOp()
class LoggerTest: CommandOpMode() {
    override fun postSelector() {
        Drivetrain.reset()
        Drivetrain.run {
            it.setWeightedDrivePower(
                Random.nextDouble(),
                Random.nextDouble(),
                Random.nextDouble(),
            )
        }.schedule()
    }
}