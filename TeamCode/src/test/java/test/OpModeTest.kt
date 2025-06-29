package test

import org.firstinspires.ftc.teamcode.command.internal.RunCommand
import org.firstinspires.ftc.teamcode.component.QuadratureEncoder
import org.firstinspires.ftc.teamcode.fakehardware.FakeGamepad
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.opmodes.ArmTest
import org.firstinspires.ftc.teamcode.opmodes.Auto
import org.firstinspires.ftc.teamcode.opmodes.Curve
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArm
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf
import org.firstinspires.ftc.teamcode.util.OpModeRunner
import org.junit.Test

class OpModeTest: TestClass(){
    init {
        OuttakeArmConf.p = 0.4
        OuttakeArmConf.d = 10.0
        (OuttakeArm.leftMotor.hardwareDevice as FakeMotor)
            .maxVelocityInTicksPerSecond = 10000
        (OuttakeArm.leftMotor.hardwareDevice as FakeMotor)
            .maxAccel *= 2
        HWManager.BulkData.callbacks.add {
            quadrature[1] = (
                    (OuttakeArm.leftMotor.hardwareDevice as FakeMotor)
                        .currentPosition.toDouble()
                    )
        }
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
}
