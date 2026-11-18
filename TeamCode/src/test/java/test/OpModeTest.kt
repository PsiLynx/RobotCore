package test

import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.While
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.reverseTangent
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.tangent
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.opmodes.DriveForwardAuto
import org.firstinspires.ftc.teamcode.opmodes.Teleop
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.subsystem.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.robot.Robot
import org.firstinspires.ftc.teamcode.util.OpModeRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.core.Logger
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.PI

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class OpModeTest: TestClass(){

    @Test fun emptyTest(){ }
    @Test fun runLightBotics(){
        OpModeRunner(
            DriveForwardAuto()
        ).run()
    }
    @Test fun runSOTM(){
        OpModeRunner(
            object : CommandOpMode() {
                override fun postSelector() {
                    Drivetrain.io.position = Pose2D(50, 50, PI)
                    ShootingStateOTM().schedule()
                    (
                        While({true},
                            followPath {
                                start(50, 50)
                                lineTo(-12, 50, tangent)
                                stop()
                                lineTo(50, 50, reverseTangent)
                                stop()
                            }
                        )
                        parallelTo While({true},
                            Robot.kickBalls()
                        )
                    ).schedule()

                }
            }
        ).run()
    }
    /*
    @Test fun runAutoBack(){
        OpModeRunner(
            AutoBackIronNest()
        ).run()
    }
    @Test fun runCurve(){

        OpModeRunner(
            @Autonomous object : CommandOpMode() {
                override fun preSelector() {
                    Drivetrain.resetLocalizer()
                }
                override fun postSelector() {
                    followPath {
                        start(0, 0)
                        lineTo(0, 10, tangent)
                        arcLeft(PI, 20, tangent)
                    }.schedule()
                }
            }
        ).run()
    }

    */
}
