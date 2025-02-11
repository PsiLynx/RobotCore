package org.ftc3825.subsystem

import com.acmerobotics.dashboard.config.Config
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Component
import org.ftc3825.component.Component.Direction.FORWARD
import org.ftc3825.component.Component.Direction.REVERSE
import org.ftc3825.component.Motor
import org.ftc3825.component.QuadratureEncoder
import org.ftc3825.component.TouchSensor
import org.ftc3825.subsystem.OuttakeArmConf.d
import org.ftc3825.subsystem.OuttakeArmConf.f
import org.ftc3825.subsystem.OuttakeArmConf.g
import org.ftc3825.subsystem.OuttakeArmConf.p
import org.ftc3825.subsystem.OuttakeArmConf.outtakeAngle
import org.ftc3825.subsystem.OuttakeArmConf.wallAngle
import org.ftc3825.subsystem.OuttakeArmConf.transferAngle
import org.ftc3825.util.degrees
import org.ftc3825.util.leftOuttakeMotorName
import org.ftc3825.util.outtakeEncoderName
import org.ftc3825.util.outtakeTouchSensorName
import org.ftc3825.util.pid.PIDFGParameters
import org.ftc3825.util.rightOuttakeMotorName
import kotlin.math.PI
import kotlin.math.abs

@Config object OuttakeArmConf {
    @JvmField var p = 2.5
    @JvmField var d = 3.0
    @JvmField var f = 0.02
    @JvmField var g = 0.03
    @JvmField var outtakeAngle = 105
    @JvmField var wallAngle = -40
    @JvmField var transferAngle = 230
}
object OuttakeArm: Subsystem<OuttakeArm> {
    private val controllerParameters = PIDFGParameters(
        P = { p },
        D = { d },
        relF = { f },
        G = { g },
    )
    val leftMotor = Motor(
        leftOuttakeMotorName,
        75,
        FORWARD,
        controllerParameters = controllerParameters,
    )
    private val rightMotor = Motor(
        rightOuttakeMotorName,
        75,
        REVERSE,
        controllerParameters = controllerParameters,
    )
    private val ticksPerRad = leftMotor.ticksPerRev / ( 2 * PI )
    private const val zeroAngle = 0.0 // TODO: change

    val touchSensor = TouchSensor(outtakeTouchSensorName)

    val position: Double
        get() = leftMotor.position
    val velocity: Double
        get() = leftMotor.velocity
    var angle: Double
        get() = leftMotor.angle
        set(value) { leftMotor.angle = value }

    val isAtBottom: Boolean
        get() = touchSensor.pressed

    override val components
        get() = arrayListOf<Component>(leftMotor, rightMotor)

    init {
        leftMotor.ticksPerRev = 2165.0
        motors.forEach {
            it.useInternalEncoder()
            it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
        }
        leftMotor.encoder = QuadratureEncoder(outtakeEncoderName, FORWARD)
        leftMotor.setpointError = { leftMotor.setpoint - leftMotor.angle }
        leftMotor.pos = { leftMotor.angle }
        leftMotor.initializeController(controllerParameters)
    }

    override fun update(deltaTime: Double) {
        if( isAtBottom ) leftMotor.angle = zeroAngle

        rightMotor.power = leftMotor.lastWrite or 0.0
    }

    fun setPowerCommand(power: Double) = run {
        setPower(power)
    } withEnd {
        setPower(0.0)
    }
    fun setPower(power: Double) {
        leftMotor.power = power
        rightMotor.power = power
    }

    fun runToPosition(pos: () -> Double) = (
        run {
            leftMotor.runToPosition(pos())
        }
            withInit {
                leftMotor.runToPosition(pos())
        }
        until {
            abs(leftMotor.angle - pos()) < 0.1
                && abs(leftMotor.encoder!!.delta) < 2
        }
            withEnd {
            //setPower(controllerParameters.F.toDouble())
            leftMotor.doNotFeedback()
            leftMotor.power = 0.0
            rightMotor.power = 0.0
        }
    )
    fun runToPosition(pos: Double) = runToPosition { pos }
    fun outtakeAngle() = runToPosition { degrees(outtakeAngle) }
    fun wallAngle() = runToPosition { degrees(wallAngle) }
    fun transferAngle() = runToPosition { degrees(transferAngle) }

    fun resetAngle() = InstantCommand { angle = 0.0 }

    fun zero() = run { setPower(-0.5) } until { isAtBottom } withEnd { setPower(0.0) }

}
