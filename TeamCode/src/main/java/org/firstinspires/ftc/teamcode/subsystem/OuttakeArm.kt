package org.firstinspires.ftc.teamcode.subsystem

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.component.Component
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import org.firstinspires.ftc.teamcode.component.Component.Direction.REVERSE
import org.firstinspires.ftc.teamcode.component.Motor
import org.firstinspires.ftc.teamcode.component.QuadratureEncoder
import org.firstinspires.ftc.teamcode.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.d
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.f
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.g
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.p
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.ramAngle
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.outtakeAngle
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.wallAngle
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.transferAngle
import org.firstinspires.ftc.teamcode.subsystem.OuttakeArmConf.useComp
import org.firstinspires.ftc.teamcode.util.control.PIDFController
import org.firstinspires.ftc.teamcode.util.degrees
import kotlin.math.abs

@Config object OuttakeArmConf {
    @JvmField var p = 0.4
    @JvmField var d = 0.35
    @JvmField var f = 0.04
    @JvmField var g = 0.04
    @JvmField var ramAngle = 115
    @JvmField var outtakeAngle = 97
    @JvmField var wallAngle = -55
    @JvmField var transferAngle = 230
    @JvmField var useComp = true
}
object OuttakeArm: Subsystem<OuttakeArm>() {
    private val controller = PIDFController(
        P = { p },
        D = { d },
        relF = { f },
        G = { g },
        pos = { leftMotor.angle },
        apply = {
            rightMotor.compPower(it)
            leftMotor .compPower(it)
        }
    )
    val leftMotor = HardwareMap.leftOuttake(FORWARD)
    private val rightMotor = HardwareMap.rightOuttake(REVERSE)

    val targetPos: Double get() = controller.targetPosition

    val position: Double
        get() = leftMotor.position
    val velocity: Double
        get() = leftMotor.velocity
    val angle: Double
        get() = leftMotor.angle

    override val components: List<Component>
        get() = arrayListOf<Component>(leftMotor, rightMotor)

    init {
        motors.forEach {
            it.setZeroPowerBehavior(Motor.ZeroPower.BRAKE)
        }
        leftMotor.useEncoder(HardwareMap.outtakeRelEncoder(
            FORWARD,
            ticksPerRev = 9754.0 * 2,
        ))
    }

    override fun update(deltaTime: Double)
        = controller.updateController(deltaTime)

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
        run { controller.targetPosition = pos() }
        withInit { controller.targetPosition = pos() }
        until {
            abs(leftMotor.angle - pos()) < 0.01
            && abs(leftMotor.encoder!!.delta) == 0.0
        }
        withEnd {
            leftMotor.power = 0.0
            rightMotor.power = 0.0
        }
    )
    fun runToPosition(pos: Double) = runToPosition { pos }
    fun ramAngle() = runToPosition { degrees(ramAngle) }
    fun outtakeAngle() = runToPosition { degrees(outtakeAngle) }
    fun wallAngle() = runToPosition { degrees(wallAngle) }
    fun transferAngle() = runToPosition { degrees(transferAngle) }

}
