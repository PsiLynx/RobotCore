package org.firstinspires.ftc.teamcode.opmodes

import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.WaitCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.reverseTangent
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import kotlin.math.PI

class AutoBack3: CommandOpMode() {
    override fun postSelector() {
        val xMul  = if (Globals.alliance == BLUE) 1 else -1

        val startPose = Pose2D(
            -5 * xMul, -62, 3 * PI / 2
        )

        (
            WaitCommand(25) andThen (
                ShootingStateOTM() racesWith (
                    followPath {
                        start(startPose.vector)
                        lineTo(-12 * xMul, 12, reverseTangent)
                    }
                    andThen (
                        WaitUntilCommand(Robot::readyToShoot)
                        withTimeout 1
                    )
                    andThen Robot.kickBalls()
                )
            )
        ).schedule()
    }
}