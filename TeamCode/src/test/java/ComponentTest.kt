package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.command.internal.CommandScheduler
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.sim.timeStep
import org.firstinspires.ftc.teamcode.util.PIDFGParameters
import org.firstinspires.ftc.teamcode.util.TestClass
import org.firstinspires.ftc.teamcode.util.assertWithin
import org.junit.Test

class ComponentTest: TestClass() {

    @Test fun testMotorRTP(){
        val motor = Motor(
            "RTPTestMotor",
            hardwareMap,
            rpm=435,
            controllerParameters = PIDFGParameters(
                P=0.1,
                I=0,
                D=0.3,
                F=0,
                G=0
            )
        )
        motor.useInternalEncoder()
        motor.runToPosition(1000.0)

        for(i in 0..5000){
            CommandScheduler.update()
            motor.update(timeStep)
        }
        assertWithin(
            motor.position - 1000.0,
            epsilon = 1E-1
        )
    }
}