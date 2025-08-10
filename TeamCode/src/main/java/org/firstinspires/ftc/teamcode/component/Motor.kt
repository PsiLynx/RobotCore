package org.firstinspires.ftc.teamcode.component

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.HardwareDevice
import org.firstinspires.ftc.teamcode.component.Component.Direction
import org.firstinspires.ftc.teamcode.util.Globals
import org.firstinspires.ftc.teamcode.component.MotorConf.nominalVoltage
import org.firstinspires.ftc.teamcode.hardware.HardwareMap

@Config object MotorConf {
    @JvmField var nominalVoltage = 13.0
}

open class Motor (
    override val hardwareDevice: HardwareDevice,
    override val name: String,
    override val port: Int,
    ioOpTime: Double,
    var direction: Direction = FORWARD,
    basePriority: Double,
    priorityScale: Double,
): Actuator(ioOpTime, basePriority, priorityScale) {
    override fun doWrite(write: Optional<Double>){
        ( hardwareDevice as DcMotor ).power = ( write or 0.0 ) * direction.dir
    }

    var encoder: Encoder? = null

    var ticks = 0.0
    private var lastTicks = 0.0

    var velocity = 0.0
    private var lastVelocity = 0.0

    var acceleration = 0.0

    var angle: Double
        get() = encoder?.angle ?: 0.0
        set(value) { encoder?.angle = value }

    var position: Double
        get() = encoder?.pos ?: 0.0
        set(value) { encoder?.pos = value }

    init { addToDash(" Motors") }

    override fun set(value: Double?) { if(value != null) power = value }

    override fun update(deltaTime: Double) {
        this.encoder?.update(deltaTime)

        lastTicks = ticks
        ticks = (encoder?.pos ?: 0.0)

        lastVelocity = velocity
        velocity = (ticks - lastTicks) / deltaTime
        acceleration = (velocity - lastVelocity) / deltaTime

    }

    override fun resetInternals() {
        ticks = 0.0
        lastTicks = 0.0
        velocity = 0.0
        lastVelocity = 0.0
        acceleration = 0.0
    }

    fun resetPosition() = this.encoder?.resetPosition() ?: Unit

    fun useEncoder(encoder: Encoder){
        this.encoder = encoder
    }

    fun useInternalEncoder(ticksPerRev: Double, wheelRadius: Double) =
        useEncoder(QuadratureEncoder(
            (hardwareDevice as DcMotor),
            direction,
            ticksPerRev,
            wheelRadius
        ))

    fun setZeroPowerBehavior(behavior: ZeroPower) {
        (hardwareDevice as DcMotor)
            .zeroPowerBehavior = zeroPowerBehaviors[behavior]
    }

    var power: Double
        get() = lastWrite or 0.0
        set(value){
            if(!value.isNaN()) {
                targetWrite = Optional(value)
            }
        }

    fun compPower(power: Double){
        this.power = power * ( nominalVoltage / Globals.robotVoltage )
    }

    enum class ZeroPower {
        FLOAT, BRAKE, UNKNOWN
    }

    companion object {
        val zeroPowerBehaviors = mapOf(
                ZeroPower.BRAKE to ZeroPowerBehavior.BRAKE,
                ZeroPower.FLOAT to ZeroPowerBehavior.FLOAT,
                ZeroPower.UNKNOWN to ZeroPowerBehavior.UNKNOWN)
    }
}
