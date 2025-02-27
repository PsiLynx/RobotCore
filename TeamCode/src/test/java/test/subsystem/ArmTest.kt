package test.subsystem

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.fakehardware.FakeMotor
import org.ftc3825.fakehardware.FakeTouchSensor
import org.ftc3825.sim.TestClass
import org.ftc3825.sim.addRule
import org.ftc3825.subsystem.OuttakeArm
import org.ftc3825.util.graph.Graph
import org.ftc3825.util.graph.Function
import org.ftc3825.util.leftOuttakeMotorName
import org.ftc3825.util.outtakeEncoderName
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs

class ArmTest: TestClass() {
    @Test fun testSetAngle() {
        OuttakeArm.reset()
        CommandScheduler.reset()

        OuttakeArm.angle = 0.0
        OuttakeArm.leftMotor.run {
            this.P = { 3.0 }
            this.D = { 30.0 }
            this.G = { 0.0 }
        }
        (
            OuttakeArm.leftMotor.hardwareDevice as FakeMotor
        ).run {
            this.maxVelocityInTicksPerSecond = 10000
        }

        val command = OuttakeArm.runToPosition(PI / 2)
        command.schedule()

        val graph = Graph(
            Function({OuttakeArm.angle}, '*'),
            Function({PI / 2}, '|'),
            min = 0.0,
            max = PI
        )
        val motor = FakeHardwareMap.get(
            DcMotor::class.java,
            outtakeEncoderName
        ) as FakeMotor

        addRule {
            motor.setCurrentPosition(
                ( OuttakeArm.leftMotor.hardwareDevice as FakeMotor )
                    .currentPosition
            )
        }

        for(i in 0..100){
            CommandScheduler.update()
            if(i % 2 == 0) {
                graph.printLine()
            }
            //if(command.isFinished()) break
        }
        assert(command.isFinished())
    }
}