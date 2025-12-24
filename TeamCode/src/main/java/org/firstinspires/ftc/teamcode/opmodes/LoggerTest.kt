package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import kotlin.random.Random

@TeleOp()
class LoggerTest: CommandOpMode() {
    override fun postSelector(){
        TankDrivetrain.reset()
        TankDrivetrain.run {
            it.setWeightedDrivePower(
                Random.nextDouble(),
                Random.nextDouble(),
                Random.nextDouble(),
            )
        }.schedule()
    }
}