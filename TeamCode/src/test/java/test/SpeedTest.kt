package test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.internal.Command
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.util.OpModeRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class SpeedTest: TestClass() {
    @Test
    fun measureSimDtSpeed(){
        OpModeRunner(
            @Autonomous object : CommandOpMode() {
                override fun initialize() {
                    (Drivetrain.run {
                        it.setWeightedDrivePower(1.0, 0.0, 0.0, 0.0, true)
                    } withTimeout 5 ).schedule()
                }
            }
        ).run()
    }
}