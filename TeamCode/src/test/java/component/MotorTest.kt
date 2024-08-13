package org.firstinspires.ftc.teamcode.test.component

import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.sim.timeStep
import org.firstinspires.ftc.teamcode.util.pid.PIDFGParameters
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.assertEqual
import org.firstinspires.ftc.teamcode.util.assertWithin
import org.firstinspires.ftc.teamcode.util.graph.*
import org.firstinspires.ftc.teamcode.util.graph.Function
import org.junit.Test

class MotorTest: TestClass() {

    @Test fun testRTP(){
        val motor = Motor(
            "RTPTestMotor",
            hardwareMap,
            rpm=435,
            controllerParameters = PIDFGParameters(
                P=0.0006,
                I=0.0000025,
                D=0.01,
                F=0,
                G=0
            )
        )
        motor.useInternalEncoder()
        motor.runToPosition(1000.0)

        val graph = Graph(
            Function({motor.position}, '*'),
            Function({1000.0}, '|'),
            min = 0.0,
            max = 1600.0
        )

        for(i in 0..5000){
            CommandScheduler.update()
            motor.update(timeStep)
            if(i % 10 == 0) {
                graph.printLine()
            }
        }
        assertWithin(
            motor.position - 1000.0,
            epsilon = 10
        )
    }
    @Test fun testSetPower(){
        val motor = Motor("test motor for component test", hardwareMap, 435)
        motor.setPower(1.0)
        assertEqual(1.0, motor.lastWrite)

    }
}