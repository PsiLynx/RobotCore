package org.firstinspires.ftc.teamcode.component

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.component.Component.Direction.FORWARD
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior
import com.qualcomm.robotcore.hardware.HardwareDevice
import org.firstinspires.ftc.teamcode.util.Globals
import kotlin.math.PI
import org.firstinspires.ftc.teamcode.component.MotorConf.nominalVoltage

@Config object MotorConf {
    @JvmField var nominalVoltage = 13.0
}

open class Motor (
    val name: String,
    rpm: Int,
    var direction: Direction = FORWARD,
    var wheelRadius: Double = 1.0,
    basePriority: Double = 1.0,
    priorityScale: Double = 1.0,
): Actuator(basePriority, priorityScale) {
    override val hardwareDevice: HardwareDevice = GlobalHardwareMap.get(DcMotor::class
        .java, name)

    override val ioOpTime = DeviceTimes.motor
    override fun doWrite(write: Write){
        ( hardwareDevice as DcMotor ).power = ( write or 0.0 ) * direction.dir
    }

    var encoder: Encoder? = null
    open var ticksPerRev = 28 * 6000.0 / rpm

    var ticks = 0.0
    private var lastTicks = 0.0

    var velocity = 0.0
    private var lastVelocity = 0.0

    var acceleration = 0.0

    var angle: Double
        get() = ( (
	    ticks
	    / ticksPerRev
	) % 1 ) * 2 * PI
	set(value) {
	    encoder?.pos = value / ( 2 * PI ) * ticksPerRev
	}

    val position: Double
        get() = ticks / ticksPerRev * wheelRadius * 2 * PI

    init { addToDash("Motors", name) }

    override fun set(value: Double?) {
        if(value == null) lastWrite = Write.empty()
        else power = value
    }

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

    fun useInternalEncoder() =
        useEncoder(QuadratureEncoder(hardwareDevice as DcMotor, direction))

    fun setZeroPowerBehavior(behavior: ZeroPower) {
        (hardwareDevice as DcMotor)
            .zeroPowerBehavior = zeroPowerBehaviors[behavior]
    }

    var power: Double
        get() = lastWrite or 0.0
        set(value){ targetWrite = Write(value) }

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
