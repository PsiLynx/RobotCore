package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.Globals.Alliance.BLUE
import kotlin.math.PI

@Autonomous
class BackLeave: CommandOpMode() {
    override fun postSelector() {
        val xMul = if (Globals.alliance == BLUE) 1    else -1
        TankDrivetrain.position = (
            Pose2D(
                4.5 * xMul,
                -72 + 12,
                PI/2 + PI/2*xMul
            )
        )

        TankDrivetrain.power(0.5, 0.0, 0.0).schedule()
    }

}