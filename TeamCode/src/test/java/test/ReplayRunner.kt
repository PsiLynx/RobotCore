package test

import org.firstinspires.ftc.teamcode.opmodes.Auto
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
        Replay(Auto(), RLOGReplay("logs.rlog")).run()
    }
}