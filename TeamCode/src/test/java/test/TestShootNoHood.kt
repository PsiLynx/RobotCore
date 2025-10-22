package test

import org.firstinspires.ftc.teamcode.geometry.Prism3D
import org.firstinspires.ftc.teamcode.geometry.Quad3D
import org.firstinspires.ftc.teamcode.geometry.Triangle3D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.geometry.Vector3D
import org.firstinspires.ftc.teamcode.sim.SimulatedArtifact
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.util.log
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.Thread.sleep
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class TestShootNoHood: TestClass() {
    @Test fun runTest(){
        val goalBottom = Prism3D(
            Triangle3D(
                Vector3D(-70.16, -63.63, 30.73),
                Vector3D(-70.16, -47.69, 35.0),
                Vector3D(-48.62, -63.44, 30.73),
            ),
            0.39
        )
        val goalBackLeft = Prism3D(
            Quad3D(
                Vector3D(-70.16, -70.19, 53.75),
                Vector3D(-43.55, -70.19, 38.62),
                Vector3D(-43.55, -70.19, 00.00),
                Vector3D(-70.16, -70.19, 00.00),
            ),
            0.39
        )
        val goalBackRight = Prism3D(
            Quad3D(
                Vector3D(-70.16, -70.19, 53.75),
                Vector3D(-70.16, -43.19, 38.63),
                Vector3D(-70.16, -43.19, 00.00),
                Vector3D(-70.16, -70.19, 00.00),
            ),
            0.39
        )

        val goalFront = Prism3D(
            Quad3D(
                Vector3D(-48.17, -63.04, 38.75),
                Vector3D(-69.83, -47.33, 38.75),
                Vector3D(-69.83, -47.33, 00.00),
                Vector3D(-48.17, -63.04, 00.00),
            ),
            -0.39
        )

        Globals.alliance = Globals.Alliance.BLUE
        val pos = Vector3D(-36, -36, 13)

        val shootingSpeed = Flywheel.getVelNoHood(
            (pos.groundPlane * Vector2D(1, -1) - Globals.goalPose).mag
        )

        val verticalSpeed = sin(Flywheel.phiNoHood) * shootingSpeed
        val horizontalSpeed = cos(Flywheel.phiNoHood) * shootingSpeed

        val pose_hist = arrayListOf<Vector3D>()

        val artifact = SimulatedArtifact(
            pos,
            Vector3D(
                - horizontalSpeed / sqrt(2.0),
                - horizontalSpeed / sqrt(2.0),
                verticalSpeed
            ),
            mutableListOf(
                goalBottom,
                goalBackLeft,
                goalBackRight,
                goalFront
            )
        )

        var t = 0.0
        val dt = 0.01
        Logger.reset()
        Logger.setTimeSource { t }
        Logger.addDataReceiver(RLOGServer())
        Logger.start()

        sleep(50)
        while(t < 4){
            Logger.periodicBeforeUser()
            artifact.update(dt)
            Logger.recordOutput("corners", (
                goalFront.top.vertices + goalFront.top.vertices[0]
                + goalFront.bottom.vertices + goalFront.bottom.vertices[0]
            ).map { it / 39.37 }.toTypedArray())

            if( ((t * 20) % 1) < 0.05 ) pose_hist.add(artifact.pos)
            Logger.recordOutput("pose_hist", pose_hist.map { it / 39.37 }.toTypedArray())

            t += dt
            sleep(10)
            Logger.periodicAfterUser(0.0, 0.0)
        }
    }
}