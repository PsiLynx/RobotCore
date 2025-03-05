package org.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.teamcode.component.Component
import org.teamcode.component.Component.Direction.FORWARD
import org.teamcode.component.Component.Direction.REVERSE
import org.teamcode.component.Motor
import org.teamcode.component.QuadratureEncoder
import org.teamcode.subsystem.OuttakeArmConf.d
import org.teamcode.subsystem.OuttakeArmConf.f
import org.teamcode.subsystem.OuttakeArmConf.g
import org.teamcode.subsystem.OuttakeArmConf.p
import org.teamcode.subsystem.OuttakeArmConf.outtakeAngle
import org.teamcode.subsystem.OuttakeArmConf.wallAngle
import org.teamcode.subsystem.OuttakeArmConf.transferAngle
import org.teamcode.subsystem.OuttakeArmConf.useComp
import org.teamcode.util.degrees
import org.teamcode.util.leftOuttakeMotorName
import org.teamcode.util.outtakeEncoderName
import org.teamcode.util.control.PIDFGParameters
import org.teamcode.util.rightOuttakeMotorName
import kotlin.math.PI
import kotlin.math.abs

@Config object OuttakeArmConf {
    @JvmField var p = 1.0
    @JvmField var d = 2.0
    @JvmField var f = 0.08
    @JvmField var g = 0.03
    @JvmField var outtakeAngle = 80
    @JvmField var wallAngle = -35
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
    private val ticksPerRad = leftMotor.ticksPerRev / ( 2 * PI )

    val position: Double
        get() = leftMotor.position
    val velocity: Double
        get() = leftMotor.velocity
    var angle: Double
        get() = leftMotor.angle
        set(value) { leftMotor.angle = value }

    override val components
        get() = arrayListOf<Component>(leftMotor, rightMotor)

    init {
        leftMotor.ticksPerRev = 20000.0
        motors.forEach {
            it.useInternalEncoder()
            it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
        }
        leftMotor.encoder = QuadratureEncoder(outtakeEncoderName, FORWARD)
        leftMotor.setpointError = { leftMotor.setpoint - leftMotor.angle }
        leftMotor.pos = { leftMotor.angle }
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
        leftMotor.ticks = 0.0
    }

}
