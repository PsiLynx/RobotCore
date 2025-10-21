package test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.QuadratureEncoder
import org.firstinspires.ftc.teamcode.fakehardware.FakeGamepad
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.opmodes.CommandOpMode
import org.firstinspires.ftc.teamcode.opmodes.dt.Curve
import org.firstinspires.ftc.teamcode.opmodes.EncoderTest
import org.firstinspires.ftc.teamcode.opmodes.fastwheel.FlywheelFullSend
import org.firstinspires.ftc.teamcode.opmodes.Teleop
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.sim.addRule
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Flywheel
import org.firstinspires.ftc.teamcode.util.OpModeRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.psilynx.psikit.ftc.HardwareMapWrapper
import org.psilynx.psikit.ftc.wrappers.MotorWrapper
import org.psilynx.psikit.ftc.wrappers.PinpointWrapper
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class OpModeTest: TestClass(){
    @Test fun runEncoderTest(){


//        OpModeRunner(
//            EncoderTest()
//        ).run()
    }
    @Test fun runFlywheel(){

//        OpModeRunner(
//            FlywheelFullSend(),
//            afterInit = {
//                RunCommand {
//                    (
//                        FakeHardwareMap.get(DcMotor::class.java, "m0")
//                        as FakeMotor
//                    ).setCurrentPosition(
//                        FakeHardwareMap
//                            .get(DcMotor::class.java, "m0")
//                            .currentPosition
//                    )
//                    println(
//                        FakeHardwareMap.get(DcMotor::class.java, "m0")
//                            .currentPosition
//                    )
//                }.schedule()
//                return@OpModeRunner true
//            }
//        ).run()
    }
    @Test fun measureSimDtSpeed(){
//        OpModeRunner(
//            @Autonomous object : CommandOpMode() {
//                override fun initialize() {
//                    (Drivetrain.run {
//                        it.setWeightedDrivePower(1.0, 0.0, 0.0, 0.0, true)
//                    } withTimeout 5 ).schedule()
//                }
//            }
//        ).run()
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
