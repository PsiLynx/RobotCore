package test.FakeHardware

import org.ftc3825.component.Component
import org.ftc3825.component.Encoder
import org.ftc3825.component.QuadratureEncoder
import org.ftc3825.fakehardware.FakeMotor
import org.ftc3825.util.TestClass

class EncoderTest: TestClass() {

    var encoder = QuadratureEncoder(
        "motor for testing quadrature encoder",
        Component.Direction.FORWARD
    )

    /*
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
            encoder.distance = dist
            assertWithin(
                (encoder.distance - dist),
                1e-6
            )
        }
    }
    @Test fun testWithMotorMoving(){
        for( i in 1..1000){
            hardwareDevice.setCurrentPosition(i)
            encoder.update()
            val dist = i
            assertWithin(
                (encoder.distance - dist),
                1e-6)
        }
    }
    */
}
