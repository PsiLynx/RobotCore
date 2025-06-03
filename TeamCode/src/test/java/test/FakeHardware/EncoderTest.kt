package test.FakeHardware

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.QuadratureEncoder
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.hardware.HWManager
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.rotations
import org.junit.Test

class EncoderTest: TestClass() {

    val name = "motor for testing quadrature encoder"
    var encoder = QuadratureEncoder(
        0,
        Component.Direction.FORWARD,
        1.0,
        1.0
    )

    @Test fun testSetAngle(){
        for( i in 1..1000){
            val angle:Double = i.toDouble() / 1000.0
            encoder.angle = angle
            assertWithin(
                (encoder.angle - angle) % rotations(1),
                1e-6
            )
        }
    }
    @Test fun testSetDist(){
        for( i in 1..1000){
            val dist = i.toDouble() / 100.0
            encoder.pos = dist
            assertWithin(
                (encoder.pos - dist),
                1e-6
            )
        }
    }
    @Test fun testWithMotorMoving(){
        for( i in 1..1000){
            HWManager.BulkData.quadrature[0] = i.toDouble()

            encoder.update(0.1)
            val dist = i
            assertWithin(
                (encoder.pos - dist),
                1e-6)
        }
    }
}
