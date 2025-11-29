package test

import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.opmodes.Teleop
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.util.Globals
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.core.rlog.RLOGDecoder
import org.psilynx.psikit.core.rlog.RLOGReplay
import org.psilynx.psikit.ftc.Replay
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class ReplayRunner {
    @Test fun runReplay(){
        Globals.logReplay = true
        Globals.running = false
        Replay(
            Teleop(),
            RLOGReplay("logs.rlog")
        )//.run()
    }
}