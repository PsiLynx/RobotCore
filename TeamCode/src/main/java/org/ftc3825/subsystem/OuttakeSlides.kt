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
    val leftMotor = Motor(
        leftOuttakeMotorName,
        1125,
        Motor.Direction.FORWARD,
        wheelRadius = inches(0.75),
    )
    val rightMotor = Motor(
        rightOuttakeMotorName,
        1125,
        Motor.Direction.REVERSE,
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
    }

    fun setPower(power: Double) {
        leftMotor.setPower(power)
        rightMotor.setPower(power)
    }

    fun runToPosition(pos: Double) = (
        run { if(pos < this.position) setPower(-0.1) else setPower(1.0) }
        until { abs(this.position - pos) < 15 }
        withEnd { setPower(0.1) }
    )


    fun extend() = (
        run { setPower(1.0) } 
        until { leftMotor.position > 1500 } 
        withEnd { setPower(0.1) }
    )

    fun retract() = (
        run { setPower(-0.2) }
        until { leftMotor.position < 10 } 
        withEnd { setPower(0.0) }
    )

}
