package test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.QuadratureEncoder
import org.firstinspires.ftc.teamcode.fakehardware.FakeGamepad
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.gvf.HeadingType
import org.firstinspires.ftc.teamcode.gvf.HeadingType.Companion.tangent
import org.firstinspires.ftc.teamcode.gvf.followPath
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.opmodes.Auto
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.opmodes.dt.Curve
import org.firstinspires.ftc.teamcode.opmodes.fastwheel.FlywheelFullSend
import org.firstinspires.ftc.teamcode.opmodes.Teleop
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.subsystem.TankDrivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.util.OpModeRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.ftc.HardwareMapWrapper
import org.psilynx.psikit.ftc.wrappers.MotorWrapper
import org.psilynx.psikit.ftc.wrappers.PinpointWrapper
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.PI

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class OpModeTest: TestClass(){
    @Test fun runAuto(){
       OpModeRunner(
           Auto()
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
