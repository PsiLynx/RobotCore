package test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.opmodes.dt.ForwardBack
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.mechanism.LoggedMechanism2d
import org.psilynx.psikit.core.mechanism.LoggedMechanismLigament2d
import org.psilynx.psikit.core.rlog.RLOGReplay
import org.psilynx.psikit.core.rlog.RLOGServer
import org.psilynx.psikit.ftc.PsiKitOpMode
import org.psilynx.psikit.ftc.Replay
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.Thread.sleep
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class PsiKitTest {

    @Test fun replayFromFile(){
        Globals.running = false
        Globals.unitTesting = true
        Globals.isSimulation = true
        Replay(
            @TeleOp object : CommandOpMode() {
                override fun initialize() { }
            },
            RLOGReplay("logs.rlog")
        ) //.run()
    }

    @Test fun runLogger(){
        val server = RLOGServer()
        Logger.addDataReceiver(server)
        Logger.start()
        Logger.periodicAfterUser(0.0, 0.0)
        val mechanism = LoggedMechanism2d(1.0, 1.0)
        val root = mechanism.getRoot("root", 0.2, 0.0)
        val arm = root.append(
            LoggedMechanismLigament2d("arm", 0.5, 60.0)
        )
        val wrist = arm.append(
            LoggedMechanismLigament2d("wrist", 0.5, 90.0)
        )
        //while (true){
        Logger.periodicBeforeUser()
        Logger.recordOutput("test", Random.nextDouble())
        Logger.recordOutput(
            "pos",
            Pose2D(sin(FakeTimer.time / 5) * 5 + 5, 5, FakeTimer.time / 2)
        )
        wrist.angle = sin(FakeTimer.time / 5) * 180 / PI
        Logger.recordOutput("mechanism", mechanism)
        Logger.periodicAfterUser(0.0, 0.0)
        sleep(20L)
        FakeTimer.addTime(0.05)
        //}
    }
}