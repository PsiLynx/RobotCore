package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.internal.RunCommand
import org.ftc3825.command.internal.WaitCommand
import org.ftc3825.command.internal.WaitUntilCommand
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.command.internal.Command
import org.ftc3825.component.Motor
import org.ftc3825.util.centimeters
import org.ftc3825.util.inches
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.command.internal.TimedCommand
import org.ftc3825.command.RunMotorToPower
import org.ftc3825.util.leftOuttakeMotorName
import org.ftc3825.util.rightOuttakeMotorName
import kotlin.math.abs
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior

object OuttakeSlides: Subsystem<OuttakeSlides>() {
    val controllerParameters = PIDFGParameters(
        P = 0.1,
        I = 0.0,
        D = 0.1,
        F = 0.0
    )
    val leftMotor = Motor(
        leftOuttakeMotorName,
        1125,
        Motor.Direction.FORWARD,
        controllerParameters = controllerParameters,
        wheelRadius = inches(0.75),
    )
    val rightMotor = Motor(
        rightOuttakeMotorName,
        1125,
        Motor.Direction.REVERSE,
        controllerParameters = controllerParameters,
        wheelRadius = inches(0.75),
    )

    val position: Double
        get() = leftMotor.position
    val velocity: Double
        get() = leftMotor.velocity

    override val motors
        get() = arrayListOf(leftMotor, rightMotor)


    init {
        motors.forEach {
            it.useInternalEncoder()
            it.motor.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE)
        }
    }

    override fun update(deltaTime: Double) {
        motors.forEach { it.update(deltaTime) }
        rightMotor.setPower(leftMotor.lastWrite ?: 0.0)
    }

    fun setPower(power: Double) {
        leftMotor.setPower(power)
        rightMotor.setPower(power)
    }

    fun runToPosition(pos: Double) = (
        run {
            leftMotor.runToPosition(pos)
            //rightMotor.runToPosition(pos)
        }
        until {
            abs(this.position - pos) < 5 
            && abs(this.leftMotor.encoder!!.delta) < 5 
        }
        withEnd {
            setPower(0.1)
            leftMotor.doNotFeedback()
            //rightMotor.doNotFeedback()
        }
    )


    fun extend() = (
        runToPosition(1600.0)
    )

    fun retract() = (
        runToPosition(0.0)
    )

}
