package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.component.QuadratureEncoder
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.d
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.f
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.g
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.p
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.outtakeAngle
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.wallAngle
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.transferAngle
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.useComp
import org.firstinspires.ftc.teamcode.util.degrees
import org.firstinspires.ftc.teamcode.util.leftOuttakeMotorName
import org.firstinspires.ftc.teamcode.util.outtakeRelEncoderName
import org.firstinspires.ftc.teamcode.util.outtakeAbsEncoderName
import org.firstinspires.ftc.teamcode.util.control.PIDFGParameters
import org.firstinspires.ftc.teamcode.util.rightOuttakeMotorName
import kotlin.math.PI
import kotlin.math.abs

@Config object OuttakeArmConf {
    @JvmField var p = 0.4
    @JvmField var d = 0.35
    @JvmField var f = 0.04
    @JvmField var g = 0.04
    @JvmField var outtakeAngle = 108
    @JvmField var wallAngle = -70
    @JvmField var transferAngle = 230
    @JvmField var useComp = true
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

    val position: Double
        get() = leftMotor.position
    val velocity: Double
        get() = leftMotor.velocity
    val angle: Double
        get() = leftMotor.angle

    override val components
        get() = arrayListOf<Component>(leftMotor, rightMotor)

    init {
        motors.forEach {
            it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
        }
        leftMotor.encoder = QuadratureEncoder(outtakeRelEncoderName, REVERSE)
        leftMotor.ticksPerRev = 9754.0 * 2
        leftMotor.setpointError = {
            arrayListOf(
                leftMotor.setpoint - leftMotor.angle,
                leftMotor.setpoint - leftMotor.angle - 2 * PI,
                leftMotor.setpoint - leftMotor.angle + 2 * PI,
                leftMotor.setpoint - leftMotor.angle - 3 * PI,
                leftMotor.setpoint - leftMotor.angle + 3 * PI,
            ).minBy { abs(it) }
        }
        leftMotor.pos = { leftMotor.angle }

        Telemetry.addAll {
            "quadrature" ids { leftMotor.encoder!!.pos }
            "angle" ids leftMotor::angle
        }
    }

    override fun update(deltaTime: Double) {
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
            leftMotor.runToPosition(pos(), useComp)
        }
        withInit {
            leftMotor.runToPosition(pos(), useComp)
        }
        until {
            abs(leftMotor.angle - pos()) < 0.01
            && abs(leftMotor.encoder!!.delta) == 0.0
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

    override fun reset() {
        super.reset()

    }

}
