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
import com.qualcomm.robotcore.hardware.TouchSensor
import org.ftc3825.command.internal.GlobalHardwareMap

object OuttakeSlides: Subsystem<OuttakeSlides>() {
    val controllerParameters = PIDFGParameters(
        P = 0.05,
        I = 0.0,
        D = 0.05,
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

    private var setpoint = 0.0

    private val touchSensor: TouchSensor = GlobalHardwareMap.get(TouchSensor::class.java, "slides")

    val position: Double
        get() = leftMotor.position
    val velocity: Double
        get() = leftMotor.velocity

    val isAtBottom: Boolean
        get() = touchSensor.isPressed

    override val motors
        get() = arrayListOf(leftMotor, rightMotor)

    private var timeoutStart = 0L


    init {
        motors.forEach {
            it.useInternalEncoder()
            it.encoder!!.reversed = 1
            it.motor.zeroPowerBehavior = ZeroPowerBehavior.BRAKE
        }
    }

    override fun update(deltaTime: Double) {
        if( touchSensor.isPressed ) leftMotor.encoder!!.reset()

        motors.forEach { it.update(deltaTime) }
        rightMotor.setPower(leftMotor.lastWrite ?: 0.0)
    }

    fun setPower(power: Double) {
        leftMotor.setPower(power)
        rightMotor.setPower(power)
    }

    fun runToPosition(pos: Double) = (
        justUpdate()
        withInit {
            timeoutStart = System.nanoTime()
            setpoint = pos
            leftMotor.runToPosition(setpoint)
        }
        until {
            (
                    abs(this.position - pos) < 5
                    && abs(this.leftMotor.encoder!!.delta) < 5

            ) || ( System.nanoTime() - timeoutStart ) > 3e9
        }
        withEnd {
            setPower(0.1)
            leftMotor.doNotFeedback()
        }
    )

    fun holdPosition(pos: Double = setpoint) = (
            justUpdate()
                withInit {
                    leftMotor.runToPosition(pos)
                }
            until { setpoint != pos }
    )

    fun breakHolding() = InstantCommand { }




    fun extend() = (
        runToPosition(1200.0)
    )

    fun retract() = (
        runToPosition(0.0) withEnd { setPower(0.0); leftMotor.doNotFeedback() }
    )

}
