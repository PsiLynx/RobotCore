package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import kotlin.math.PI

@Autonomous
class AutoFront: CommandOpMode() {
    override fun postSelector() {
        val xMul = if (Globals.alliance == BLUE) 1    else -1
        TankDrivetrain.position = (
            Pose2D(-43.622*xMul, 63.425, 3*PI/2)
        )

        (
            ShootingStateOTM() parallelTo (
                ( TankDrivetrain.power(0.5, 0.3*xMul) withTimeout 0.5 )
                andThen WaitCommand(1)
                andThen ( Robot.kickBalls() withTimeout 3 )
                andThen (
                    TankDrivetrain.power(0.3, 0.3 * xMul)
                )
            )
        ).schedule()
    }
}