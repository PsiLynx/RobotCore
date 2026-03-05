package test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.ShootingStateOTM
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.command.internal.WaitUntilCommand
import org.firstinspires.ftc.teamcode.command.internal.controlFlow.While
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.reverseTangent
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.tangent
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.opmodes.Auto12Sorted
import org.firstinspires.ftc.teamcode.opmodes.AutoBackIronNest
import org.firstinspires.ftc.teamcode.opmodes.Auto12Ball
import org.firstinspires.ftc.teamcode.opmodes.Auto6LightBotics
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Turret
import org.firstinspires.ftc.teamcode.util.OpModeRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.PI

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class OpModeTest: TestClass(){

    @Test fun runLightBotics(){
        OpModeRunner(
            Auto6LightBotics()
        ).run()
    }
    @Test fun runAuto15(){
       OpModeRunner(
           Auto12Ball()
       ).run()
    }
    @Test fun runSOTM(){
        OpModeRunner(
            object : CommandOpMode() {
                override fun postSelector() {
                    ShootingStateOTM().schedule()
                    (
                        followPath {
                            start(0.0, 0.0)
                            lineTo(-30, 30, tangent)
                            stop()
                            lineTo(-12, 60, tangent)
                            stop()
                            lineTo(-60, 12, reverseTangent)
                            stop()
                            lineTo(-12, 60, tangent)
                            stop()
                            lineTo(-60, 12, reverseTangent)
                            stop()
                            lineTo(-12, 60, tangent)
                            stop()
                            lineTo(-60, 12, reverseTangent)
                            stop()
                            lineTo(-12, 60, tangent)
                            stop()
                            lineTo(-60, 12, reverseTangent)
                            stop()
                            lineTo(-12, 60, tangent)
                            stop()
                            lineTo(-60, 12, reverseTangent)
                            stop()
                            lineTo(-12, 60, tangent)
                            stop()
                            lineTo(-60, 12, reverseTangent)
                            stop()
                            lineTo(-12, 60, tangent)
                            stop()
                            lineTo(-60, 12, reverseTangent)
                            stop()
                        }.withConstraints(
                            aMax = 20.0,
                            dMax = 20.0,
                            maxVel = 40.0
                        )
                        parallelTo While({true},
                            WaitUntilCommand(Robot::readyToShoot)
                            andThen Robot.kickBalls()
                        )
                    ).schedule()

                }
            }
        ).run()
    }
    @Test fun runAutoBack(){
        OpModeRunner(
            AutoBackIronNest()
        ).run()
    }
    @Test fun runCurve(){

        OpModeRunner(
            @Autonomous object : CommandOpMode() {
                override fun preSelector() {
                    TankDrivetrain.resetLocalizer()
                }
                override fun postSelector() {
                    followPath {
                        start(0, 0)
                        lineTo(0, 10, tangent)
                        arcLeft(PI, 20, tangent)
                    }.schedule()
                    RunCommand { Thread.sleep(10) }.schedule()
                }
            }
        ).run()
    }
    @Test fun measureSimDtSpeed(){
//        OpModeRunner(
    }
    /*
    init {
        OuttakeArmConf.p = 0.4
        OuttakeArmConf.d = 10.0
        (OuttakeArm.leftMotor.hardwareDevice as FakeMotor)
            .maxVelocityInTicksPerSecond = 10000
        (OuttakeArm.leftMotor.hardwareDevice as FakeMotor)
            .maxAccel *= 2
    }
    @Test fun testAuto(){
        OpModeRunner(
            Auto(),
        ).run()
    }
    @Test fun testCurve(){
        OpModeRunner(
            Curve(),
            afterInit = {
                (it.gamepad1 as FakeGamepad).press("y")
                true
            }
        ).run()

    }
    @Test fun testOuttakeArm(){

        OpModeRunner(
            ArmTest(),
            afterInit = {
                (it.gamepad1 as FakeGamepad).press("y")
                return@OpModeRunner true
            },
        ).run()
    }
    */
}
