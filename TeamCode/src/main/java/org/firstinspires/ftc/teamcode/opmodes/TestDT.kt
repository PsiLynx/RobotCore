package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.opmodes.DtSpeeds.left
import org.firstinspires.ftc.teamcode.opmodes.DtSpeeds.right
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain

@Config object DtSpeeds {
    @JvmField var left = 0.0
    @JvmField var right = 0.0
}
@TeleOp(group = "a")
class TestDT: CommandOpMode() {
    override fun preSelector() {
        TankDrivetrain
    }
    override fun postSelector() {
        TankDrivetrain.run {
            it.differentialPowers(left, right)
        }.schedule()
    }

}