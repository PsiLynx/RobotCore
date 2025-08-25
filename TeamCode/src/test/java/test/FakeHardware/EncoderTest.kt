package test.FakeHardware

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.QuadratureEncoder
import org.firstinspires.ftc.teamcode.fakehardware.FakeHardwareMap
import org.firstinspires.ftc.teamcode.fakehardware.FakeMotor
import org.firstinspires.ftc.teamcode.sim.TestClass
import org.firstinspires.ftc.teamcode.util.rotations
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import test.ShadowAppUtil
import kotlin.math.PI

@Config(shadows = [ShadowAppUtil::class])
@RunWith(RobolectricTestRunner::class)
class EncoderTest: TestClass() {

    val name = "motor for testing quadrature encoder"
    val motor = FakeHardwareMap.get(DcMotor::class.java, name) as FakeMotor
    var encoder = QuadratureEncoder(
        motor,
        Component.Direction.FORWARD,
        1.0,
        1 / ( 2 * PI )
    )

    @Test fun testSetAngle(){
        for( i in 1..1000){
            val angle:Double = i.toDouble() / 1000.0
            encoder.angle = angle
            assertWithin(
                (encoder.angle - angle) % rotations(1),
                1e-9
            )
        }
    }
    @Test fun testSetDist(){
        for( i in 1..1000){
            val dist = i.toDouble() / 100.0
            encoder.pos = dist
            assertWithin(
                (encoder.pos - dist),
                1e-9
            )
        }
    }
    @Test fun testWithMotorMoving(){
        motor.setCurrentPosition(0.0)
        encoder.update(0.1)
        encoder.resetPosition()
        for( i in 1..1000){
            val dist = i
            motor.setCurrentPosition(dist)

            encoder.update(0.1)
            println(encoder.pos)
            assertWithin(
                (encoder.pos - dist),
                1e-9)
        }
    }
}
