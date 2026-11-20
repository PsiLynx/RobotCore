package org.firstinspires.ftc.teamcode.component

import org.firstinspires.ftc.teamcode.OctoQuadFWv3
import org.firstinspires.ftc.teamcode.geometry.Pose2D
import org.firstinspires.ftc.teamcode.geometry.Vector2D
import org.firstinspires.ftc.teamcode.util.log
import kotlin.math.PI

class OctoQuad(
    private val deviceSupplier: () -> OctoQuadFWv3?,
    xPort: Int,
    yPort: Int,
    ticksPerMM: Double,
    offset: Vector2D,
    xDirection: Direction,
    yDirection: Direction,
    headingScalar: Double,
    velocityInterval: Int = 25
): Component() {

    private var _hwDeviceBacker: OctoQuadFWv3? = null
    override val hardwareDevice: OctoQuadFWv3 get() {
        if(_hwDeviceBacker == null){
            _hwDeviceBacker = deviceSupplier() ?: error(
                "tried to access hardware before OpMode init"
            )
        }
        return _hwDeviceBacker!!
    }

    var startPos = Pose2D(0, 0, PI / 2)

    var data = hardwareDevice.readLocalizerData()

    private var _position: Pose2D

    val position: Pose2D
        get() = _position + startPos

    var velocity: Pose2D
        private set

    private var ocPos = Pose2D()
    private var ocVel = Pose2D()
    private var crcOk = true

    init {
        _position = data.position
        velocity = data.velocity

        hardwareDevice.setAllLocalizerParameters(
            xPort,
            yPort,
            ticksPerMM.toFloat(),
            ticksPerMM.toFloat(),
            offset.x.toFloat(),
            offset.y.toFloat(),
            headingScalar.toFloat(),
            velocityInterval
        )
        hardwareDevice.setSingleEncoderDirection(xPort, xDirection)
        hardwareDevice.setSingleEncoderDirection(yPort, yDirection)

        hardwareDevice.resetLocalizerAndCalibrateIMU()
    }

    override fun resetInternals() {
        hardwareDevice.resetLocalizerAndCalibrateIMU()
        update(0.0)
    }

    override fun update(deltaTime: Double) {
        data = hardwareDevice.readLocalizerData()

        ocPos = data.position
        ocPos.heading = ocPos.heading.normalized()

        ocVel = data.velocity

        velocity = (
            if(crcOk) ocVel rotatedBy startPos.heading
            else velocity
        )

        _position =
            if(crcOk) ( ocPos rotatedBy startPos.heading )
            else _position + ( velocity * deltaTime )
        log("ocpos") value ocPos
        log("startpos") value startPos

    }

    fun setStart(value: Pose2D) {
        startPos = value
        hardwareDevice.resetLocalizerAndCalibrateIMU()
    }

    fun setPos(value: Pose2D) {
        update(0.0)
        println("OctoQuad: ")
        startPos = value - _position //+ startPos
    }

}
