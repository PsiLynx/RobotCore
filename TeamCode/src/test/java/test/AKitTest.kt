package test

import org.psilynx.psikit.Logger
import org.psilynx.psikit.RLOGServer
import org.psilynx.psikit.mechanism.LoggedMechanism2d
import org.psilynx.psikit.mechanism.LoggedMechanismLigament2d
import org.firstinspires.ftc.teamcode.sim.FakeTimer
import org.firstinspires.ftc.teamcode.util.geometry.Pose2D
import org.junit.Test
import java.lang.Thread.sleep
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class AKitTest {
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